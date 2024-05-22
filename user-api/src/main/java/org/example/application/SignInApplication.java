package org.example.application;

import lombok.RequiredArgsConstructor;
import org.example.config.JwtAuthenticationProvider;
import org.example.domain.SignInForm;
import org.example.domain.common.UserType;
import org.example.domain.model.Customer;
import org.example.domain.model.Seller;
import org.example.exception.CustomException;
import org.example.exception.ErrorCode;
import org.example.service.customer.CustomerService;
import org.example.service.seller.SellerService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignInApplication {

    private final CustomerService customerService;
    private final SellerService sellerService;
    private final JwtAuthenticationProvider provider;
    public String customerLoginToken(SignInForm form){
        // 1. 로그인 가능 여부
        Customer customer = customerService.findValidCustomer(form.getEmail(), form.getPassword())
                .orElseThrow(()->new CustomException(ErrorCode.LOGIN_CHECK_FAIL));
        // 2. 토큰 발행
        // 3. 토큰 response
        return provider.createToken(customer.getEmail(), customer.getId(), UserType.CUSTOMER);
    }

    public String sellerLoginToken(SignInForm form){
        // 1. 로그인 가능 여부
        Seller seller = sellerService.findValidSeller(form.getEmail(), form.getPassword())
                .orElseThrow(()->new CustomException(ErrorCode.LOGIN_CHECK_FAIL));
        // 2. 토큰 발행
        // 3. 토큰 response
        return provider.createToken(seller.getEmail(), seller.getId(), UserType.SELLER);
    }
}
