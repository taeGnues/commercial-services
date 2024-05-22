package org.example.client.service;

import org.example.client.service.test.EmailSendService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailSendServiceTest {

    @Autowired
    private EmailSendService emailSendService;



    @Test
    public void EamilTest() {

        String response = emailSendService.sendEmail();
        System.out.println(response);
    }

}