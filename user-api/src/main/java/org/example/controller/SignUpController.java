package org.example.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.example.application.SignUpApplication;
import org.example.domain.SignUpForm;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = "회원가입 관련")
@RestController
@RequestMapping("/signup")
@RequiredArgsConstructor
public class SignUpController {
    private final SignUpApplication signUpApplication;

    @PostMapping
    public ResponseEntity<String> customerSignUp(@RequestBody SignUpForm form){
        return ResponseEntity.ok(signUpApplication.customerSignUp(form));
    }

    @GetMapping("/verify/customer")
    public ResponseEntity<String> verifyCustomer(String email, String code){
        signUpApplication.customerVerify(email, code);
        return ResponseEntity.ok("인증이 완료되었습니다.");
    }
}
