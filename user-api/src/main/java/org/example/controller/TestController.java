package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.client.service.test.EmailSendService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final EmailSendService emailSendService;

    @GetMapping("/test")
    public ResponseEntity<String> sendTestEmail(){
        return ResponseEntity.ok(emailSendService.sendEmail());
    }

}
