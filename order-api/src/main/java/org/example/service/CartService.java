package org.example.service;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.client.RedisClient;
import org.example.domain.product.AddProductCartForm;
import org.example.domain.redis.Cart;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final RedisClient redisClient;

    public Cart getCart(Long customerId){
        return redisClient.get(customerId, Cart.class);
    }

    // Cart에 대한 추가.
    public Cart addCart(Long customerId, AddProductCartForm form){
        Cart cart = redisClient.get(customerId, Cart.class);

        // 생성된 카트가 없었다면 생성해주기.
        if (cart == null){
            cart = new Cart();
            cart.setCustomerId(customerId);
        }

        // 이전에 같은 상품이 있냐?
        Optional<Cart.Product> productOptional = cart.getProducts().stream()
                .filter(product -> product.getId().equals(form.getProductId()))
                .findFirst();

        if(productOptional.isPresent()){
            // 넣으려는 상품이 이미 있는 경우
            Cart.Product redisProduct = productOptional.get();
            List<Cart.ProductItem> items = form.getItems().stream().map(Cart.ProductItem::from).collect(Collectors.toList());

            // redis에 있는 정보를 빠르게 조회하기 위해 MAP 사용
            Map<Long, Cart.ProductItem> redisItemMap = redisProduct.getItems().stream()
                    .collect(Collectors.toMap(Cart.ProductItem::getId, it->it));

            // 1. 상품명이 달라졌다면 메세지에 담아 알려주기
            if(!redisProduct.getName().equals(form.getName())){
                cart.addMessage(redisProduct.getName() + "의 정보가 변경됐습니다. 확인 부탁드립니다.");
            }

            // 2.
            for (Cart.ProductItem item : items) {
                Cart.ProductItem redisItem = redisItemMap.get(item.getId());

                if (redisItem == null) {
                    redisProduct.getItems().add(item);
                    // happy case -> 그냥 product Item을 추가하면됨.
                } else {

                    // 장바구니의 가격과 저장하려는 항목의 가격이 달라졌다면 수정해주기.
                    if (!redisItem.getPrice().equals(item.getPrice())) {
                        cart.addMessage(redisProduct.getName() + item.getName() + "의 가격이 변경됐습니다.");
                    }

                    redisItem.setCount(redisItem.getCount() + item.getCount());
                }
            }

        }else{

            // 이전에 같은 상품이 없는 경우 그냥 추가하면 끝.
            Cart.Product product = Cart.Product.from(form);
            cart.getProducts().add(product);
            redisClient.put(customerId, cart);

            return cart;
        }


        // 카트 넣기.
        redisClient.put(customerId, cart);
        return cart;
    }
}
