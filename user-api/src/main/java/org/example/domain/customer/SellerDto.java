package org.example.domain.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.domain.model.Seller;

@Getter
@Setter
@AllArgsConstructor
public class SellerDto {
    Long id;
    String email;

    public static SellerDto from(Seller seller){
        return new SellerDto(seller.getId(), seller.getEmail());
    }
}
