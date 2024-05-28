package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.application.CartApplication;
import org.example.config.JwtAuthenticationProvider;
import org.example.domain.product.AddProductCartForm;
import org.example.domain.redis.Cart;
import org.example.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/cart")
@RequiredArgsConstructor
public class CustomerCartController {

    // 임시
    private final CartApplication cartApplication;
    private final JwtAuthenticationProvider provider;

    @PostMapping
    public ResponseEntity<Cart> addCart(
            @RequestHeader(name = "X-AUTH-TOKEN") String token,
            @RequestBody AddProductCartForm form){
        return ResponseEntity.ok(cartApplication.addCart(provider.getUserVo(token).getId(), form));
    }

    @GetMapping
    public ResponseEntity<Cart> showCart(
            @RequestHeader(name = "X-AUTH-TOKEN") String token
    ){
        return ResponseEntity.ok(cartApplication.getCart(provider.getUserVo(token).getId()));
    }
}
