package org.example.client.service.test;

import org.example.service.SignUpCustomerService;
import org.example.domain.SignUpForm;
import org.example.domain.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.time.LocalDate;

@SpringBootTest
public class SignUpCustomerServiceTest {

    @Autowired
    private SignUpCustomerService service;

    @Test
    public void signUp() {
        SignUpForm form = SignUpForm.builder()
                .name("name")
                .birth(LocalDate.now())
                .email("abc@gmail.com")
                .password("1")
                .phone("01000000")
                .build();
        Customer customer = service.signUp(form);
        System.out.println(customer.getId());
        Assert.isTrue(customer.getId()!=null);
    }
}