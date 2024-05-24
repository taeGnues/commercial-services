package org.example.domain.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductForm {

    private Long id;
    private Long productId;
    private String name;
    private String description;
    private List<UpdateProductItemForm> items;
}
