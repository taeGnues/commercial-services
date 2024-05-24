package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.model.Product;
import org.example.domain.model.ProductItem;
import org.example.domain.product.AddProductItemForm;
import org.example.domain.repository.ProductItemRepository;
import org.example.domain.repository.ProductRepository;
import org.example.exception.CustomException;
import org.example.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.exception.ErrorCode.NOT_FOUND_PRODUCT;
import static org.example.exception.ErrorCode.SAME_ITEM_NAME;

@Service
@RequiredArgsConstructor
public class ProductItemService {
    private final ProductRepository productRepository;
    private final ProductItemRepository productItemRepository;

    @Transactional
    public Product addProductItem(Long sellerId, AddProductItemForm form){
        Product product = productRepository.findBySellerIdAndId(sellerId, form.getProductId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));

        if(product.getProductItems().stream()
                .anyMatch(productItem -> productItem.getName().equals(form.getName()))){
            throw new CustomException(SAME_ITEM_NAME);
        }

        ProductItem productItem = ProductItem.of(sellerId, form);
        product.getProductItems().add(productItem);
        // fetch로 정보를 다가져왔기 때문에, 해당 entity를 사용할 수 있음.

        return product;
    }
}
