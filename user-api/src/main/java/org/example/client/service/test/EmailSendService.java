package org.example.client.service.test;

import feign.Response;
import lombok.RequiredArgsConstructor;
import org.example.client.MailgunClient;
import org.example.client.mailgun.SendMailForm;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSendService {
    private final MailgunClient mailgunClient;
    public String sendEmail(){
        SendMailForm form = SendMailForm.builder()
                .from("hi563@g.skku.edu")
                .to("hi563@naver.com")
                .subject("Test email from naver.com")
                .text("my text")
                .build();


        return mailgunClient.sendEmail(form).getBody();
    }
}
