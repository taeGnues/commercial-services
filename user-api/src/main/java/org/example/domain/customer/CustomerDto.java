package org.example.domain.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.domain.model.Customer;

@Getter
@Setter
@AllArgsConstructor
public class CustomerDto {
    private Long id;
    private String email;

    public static CustomerDto from(Customer customer){
        return new CustomerDto(customer.getId(), customer.getEmail());
    }
}
