package org.example.application;

import lombok.RequiredArgsConstructor;
import org.example.config.JwtAuthenticationProvider;
import org.example.domain.SignInForm;
import org.example.domain.common.UserType;
import org.example.domain.model.Customer;
import org.example.exception.CustomException;
import org.example.exception.ErroCode;
import org.example.service.CustomerService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignInApplication {

    private final CustomerService customerService;
    private final JwtAuthenticationProvider provider;
    public String customerLoginToken(SignInForm form){
        // 1. 로그인 가능 여부
        Customer customer = customerService.findValidCustomer(form.getEmail(), form.getPassword())
                .orElseThrow(()->new CustomException(ErroCode.LOGIN_CHECK_FAIL));
        // 2. 토큰 발행
        // 3. 토큰 response
        return provider.createToken(customer.getEmail(), customer.getId(), UserType.CUSTOMER);
    }
}
