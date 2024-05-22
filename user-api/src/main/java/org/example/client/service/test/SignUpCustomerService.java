package org.example.client.service.test;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.domain.SignUpForm;
import org.example.domain.model.Customer;
import org.example.domain.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpCustomerService {

    private final CustomerRepository customerRepository;

    public Customer signUp(SignUpForm form){
        return customerRepository.save(Customer.from(form));
    }
}
