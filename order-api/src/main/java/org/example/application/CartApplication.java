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

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartApplication {
    private final CartService cartService;
    private final ProductSearchService productSearchService; // 상품 사라지는 것을 확인하기 위한 작업

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
                    Integer currentCount = currentItemCountMap.get(formItem.getId());
                    return formItem.getCount()+cartCount > currentCount;// 최대 상품 개수 보다 많아지면 cart에 담을 수 없음.
                }
        );

    }

}
