package org.example.application;

import lombok.RequiredArgsConstructor;
import org.example.client.UserClient;
import org.example.client.user.ChangeBalanceForm;
import org.example.client.user.CustomerDto;
import org.example.domain.model.ProductItem;
import org.example.domain.redis.Cart;
import org.example.exception.CustomException;
import org.example.exception.ErrorCode;
import org.example.service.ProductItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

import static org.example.exception.ErrorCode.ORDER_FAIL_NO_MONEY;

@Service
@RequiredArgsConstructor
public class OrderApplication {
    private final CartApplication cartApplication;
    private final UserClient userClient;
    private final ProductItemService productItemService;

    // 결제를 위해 필요한 것
    // 1. 물건들이 전부 주문 가능한 상태인지 확인
    // 2. 가격 변동이 있었는지에 대해 확인
    // 3. 고객의 돈이 충분한지
    // 4. 결제 & 상품의 재고 관리

    @Transactional
    public void order(String token, Cart cart){
        // 1. 주문 시 기존 카트 버림
        // 2. 선택 주문 (내가 사지 않은 아이템을 살리기) -- 숙제

        Cart orderCart = cartApplication.refreshCart(cart); // 장바구니 변경 내역 확인 처리.
        if (orderCart.getMessages().size()>0){
            // 문제가 있음.
            throw new CustomException(ErrorCode.NOT_FOUND_PRODUCT);
        }

        CustomerDto customerDto = userClient.getCustomerInfo(token).getBody();
        int totalPrice = getTotalPrice(cart);
        if(customerDto.getBalance() < totalPrice){ // 예치금보다 적을 경우
            throw new CustomException(ORDER_FAIL_NO_MONEY);
        }


        // 이 부분에서 오류 발생 시 롤백 계획에 대해서 생각해봐야함. (Transaction 보장 XX)
        userClient.changeBalance(token, ChangeBalanceForm.builder()
                        .from("USER")
                        .message("ORDER")
                        .money(-totalPrice)
                        .build());

        for(Cart.Product product : orderCart.getProducts()){
            for(Cart.ProductItem cartItem : product.getItems()){
                ProductItem productItem = productItemService.getProductItem(cartItem.getId());
                productItem.setCount(productItem.getCount() - cartItem.getCount());
            }
        }

    }

    public Integer getTotalPrice(Cart cart){

        return cart.getProducts().stream().flatMapToInt(
                product -> product.getItems().stream()
                        .flatMapToInt(
                                productItem -> IntStream.of(productItem.getPrice()*productItem.getCount())
                        )
                )
                .sum();
    }
}
