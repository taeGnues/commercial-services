package org.example.application;

import lombok.RequiredArgsConstructor;
import org.example.client.MailgunClient;
import org.example.client.mailgun.SendMailForm;
import org.example.domain.SignUpForm;
import org.example.domain.model.Customer;
import org.example.exception.CustomException;
import org.example.exception.ErroCode;
import org.example.service.SignUpCustomerService;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SignUpApplication {
    private final MailgunClient mailgunClient;
    private final SignUpCustomerService signUpCustomerService;

    public void customerVerify(String email, String code){
        signUpCustomerService.verifyEmail(email, code);
    }

    public String customerSignUp(SignUpForm form){
        if(signUpCustomerService.isEmailExist(form.getEmail())){ // 이메일이 이미 존재할떄.
            throw new CustomException(ErroCode.ALREADY_REGISTER_USER);
        }else{

            // 회원가입 시도 후 성공 시 이메일 전송 진행함.
            Customer c = signUpCustomerService.signUp(form);
            LocalDateTime now = LocalDateTime.now();
            String code = getRandomCode();

            SendMailForm sendMailForm = SendMailForm.builder()
                    .from("hi563@g.skku.edu")
                    .to("hi563@naver.com")
                    .subject("Verification Email")
                    .text(getVerificationEmailBody(form.getEmail(), form.getName(), code))
                    .build();

            mailgunClient.sendEmail(sendMailForm);
            signUpCustomerService.changeCustomerValidateEmail(c.getId(), code);
            return "회원 가입에 성공했습니다.";
        }
    }

    private String getRandomCode(){
        return RandomStringUtils.random(10, true, true); // 랜덤 문자 숫자 생성
    }

    private String getVerificationEmailBody(String email, String name, String code){
        StringBuilder builder = new StringBuilder();
        return builder.append("Hello ").append(name).append("! Please Click Link for verification.\n\n")
                .append("http://localhost:8081/signup/verify/customer?email=")
                .append(email)
                .append("&code=")
                .append(code).toString();
    }

}
