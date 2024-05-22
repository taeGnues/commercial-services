package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.application.SignInApplication;
import org.example.domain.SignInForm;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/signIn")
@RequiredArgsConstructor
public class SignInController {

    private final SignInApplication signInApplication;
    @PostMapping("/customer")
    public ResponseEntity<String> signInCustomer(@RequestBody SignInForm form){
        // 토큰 발행.
        return ResponseEntity.ok(signInApplication.customerLoginToken(form));
    }
}
