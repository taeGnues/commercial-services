package org.example.client;

import feign.Response;
import org.example.client.mailgun.SendMailForm;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "mailgun", url="https://api.mailgun.net/v3/")
@Qualifier("mailgun") // 구분용. 여러개의 mailgun을 사용함.
public interface MailgunClient {

    // 메일보내기 기능
    @PostMapping("sandbox6915d0e965d242faa03ca69d9c3169af.mailgun.org/messages")
    ResponseEntity<String> sendEmail(@SpringQueryMap SendMailForm form);
}
