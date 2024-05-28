package org.example.application;

import lombok.RequiredArgsConstructor;
import org.example.domain.model.Product;
import org.example.domain.model.ProductItem;
import org.example.domain.product.AddProductCartForm;
import org.example.domain.redis.Cart;
import org.example.exception.CustomException;
import org.example.exception.ErrorCode;
import org.example.service.CartService;
import org.example.service.ProductSearchService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartApplication {
    private final CartService cartService;
    private final ProductSearchService productSearchService; // 상품 사라지는 것을 확인하기 위한 작업

    // 1. 장바구니에 상품을 추가했다.
    // 2. 상품의 가격이나 수량이 변동된다. ~> 처리 필요
    public Cart getCart(Long customerId) {
        Cart cart = refreshCart(cartService.getCart(customerId));

        // 반환할 Cart
        Cart retunrCart = new Cart();
        retunrCart.setCustomerId(customerId);
        retunrCart.setProducts(cart.getProducts());
        retunrCart.setMessages(cart.getMessages());

        // Redis에 저장할 Cart (messages가 비어있어야함.)
        cart.setMessages(new ArrayList<>());
        cartService.putCart(customerId, cart);

        return retunrCart;

        // 2. 메세지를 보고 난 다음에는 이미 본 메세지는 스팸이 되기 때문에 제거한다.
    }
    private Cart refreshCart(Cart cart){
        // 1. 상품이나 상품의 아이템의 정보, 가격, 수량이 변동되었는지 체크하고 그에 맞는 알람을 제공한다.
        // 2. 상품의 수량, 가격을 우리가 임의로 변경한다.

//        Map<Long, Cart.Product> productMap = cart.getProducts().stream()
//                .collect(Collectors.toMap(Cart.Product::getId, p->p));

        // product에 실제 있는 상품들을 가져오기.
        Map<Long, Product> productMap = productSearchService.getListByProductIds(
                cart.getProducts().stream().map(Cart.Product::getId).collect(Collectors.toList())
        )
                .stream()
                .collect(Collectors.toMap(Product::getId, p->p));

        //iterate 대신 for 문을 쓰는 이유? -> 상태 정보를 변경하므로
        List<String> tmpMessages = new ArrayList<>();
        for(int i = 0 ; i< cart.getProducts().size();i++){
            Cart.Product cartProduct = cart.getProducts().get(i);


            Product p = productMap.get(cartProduct.getId());
            if(p==null){ // 없는 상품은 없애기.
                cart.getProducts().remove(cartProduct);
                i--;
                tmpMessages.add(cartProduct.getName() + " 상품이 삭제됐습니다.");
                continue;
            }


            Map<Long, ProductItem> productItemMap = p.getProductItems().stream()
                    .collect(Collectors.toMap(ProductItem::getId, productItem->productItem));

            // 각각 케이스 별로 에러를 쪼개고 에러가 정상 출력되야하는지 체크.
            for(int j = 0 ; j < cartProduct.getItems().size();j++){
                Cart.ProductItem cartProductItem = cartProduct.getItems().get(j);
                ProductItem pi = productItemMap.get(cartProductItem.getId());

                if(p==null){ // 없는 상품은 없애기.
                    cartProduct.getItems().remove(cartProductItem);
                    j--;
                    cart.addMessage(cartProduct.getName() + " 옵션이 삭제됐습니다.");
                    continue;
                }


                boolean isPriceChanged = false, isCountNotEnough=false;
                if (!cartProductItem.getPrice().equals(pi.getPrice())){ // 가격이 안맞음.
                    isPriceChanged=true;
                    cartProductItem.setPrice(pi.getPrice()); // 변동된 가격 반영
                }
                if(cartProductItem.getCount() > pi.getCount()){ // 재고량 부족.
                    isCountNotEnough=true;
                    cartProductItem.setCount(pi.getCount());
                }

                if(isPriceChanged && isCountNotEnough){
                    tmpMessages.add(cartProduct.getName() + " 가격과 수량이 변동되어 구매 가능한 최대치로 변동됐습니다.");
                }else if (isPriceChanged){
                    tmpMessages.add(cartProduct.getName() + " 가격이 변동됐습니다.");
                } else if (isCountNotEnough) {
                    tmpMessages.add(cartProduct.getName() + " 수량이 변동됐습니다.");
                }
            }

            // Product에 있던 item1, item2가 만약에 다 remove됐다면 카트에서도 삭제해야함.
            if(cartProduct.getItems().size()==0){
                cart.getProducts().remove(cartProduct);
                i--;
                cart.addMessage(cartProduct.getName() + " 상품의 옵션이 모두 없어져 구매가 불가능합니다.");
                continue;
            } else if(tmpMessages.size()>0){
                StringBuilder builder = new StringBuilder();
                builder.append(cartProduct.getName() + " 상품의 변동 사항 :");
                for(String message : tmpMessages){
                    builder.append(message);
                    builder.append(", ");
                }
                cart.addMessage(builder.toString());
            }

        }
        cartService.putCart(cart.getCustomerId(), cart);
        return cart;
    }

    public Cart addCart(Long customerId, AddProductCartForm form){
        Product product = productSearchService.getByProductId(form.getProductId());
        Cart cart = cartService.getCart(customerId);

        if(cart != null && !addAble(cart, product, form)){ // Cart에 이미 추가하려는 상품이 존재하는데, 재고량때문에 상품을 추가할 수 없는 상황이면.
            throw new CustomException(ErrorCode.ITEM_COUNT_NOT_ENOUGH);
        }

        return cartService.addCart(customerId, form);

    }

    // cart에 추가하려는 상품이 있고, 해당 상품의 재고량을 확인한 후 기존에 있는 상품 수 + 장바구니에 추가한 상품 수가 해당 상품의 재고량 이상인지 확인하는 함수.
    private boolean addAble(Cart cart, Product product, AddProductCartForm form){
        Cart.Product cartProduct = cart.getProducts() // Cart에 있는 상품들 중 ID != 추가하려는 상품 ID 일때 => 새로 추가해야하는 상품이므로 여기서 처리하지 않는다.
                .stream()
                .filter(p -> p.getId().equals(form.getProductId()))
                .findFirst().orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

        Map<Long, Integer> cartItemCount = cartProduct.getItems().stream() // 현재 cart에 있는 product 개수확인.
                .collect(Collectors.toMap(Cart.ProductItem::getId, Cart.ProductItem::getCount));

        Map<Long, Integer> currentItemCountMap = product.getProductItems().stream() // 현재 product의 개수 확인.
                .collect(Collectors.toMap(ProductItem::getId, ProductItem::getCount));

        return form.getItems().stream().noneMatch(
                formItem -> {
                    Integer cartCount = cartItemCount.get(formItem.getId());
                    if(cartCount==null){
                        cartCount=0;
                    }
                    Integer currentCount = currentItemCountMap.get(formItem.getId());
                    return formItem.getCount()+cartCount > currentCount;// 최대 상품 개수 보다 많아지면 cart에 담을 수 없음.
                }
        );

    }

    /*
     * 엣지 케이스
     */
    public Cart updateCart(Long id, Cart cart) {
        // 실질적으로 변하는 데이터
        // 상품의 삭제, 수량 변경
        // refresh cart에 녹아있음.
        cartService.putCart(id, cart);
        return getCart(id);
    } // 장바구니에서 가격이 변동될까?
}
