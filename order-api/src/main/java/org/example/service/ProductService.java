package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.model.Product;
import org.example.domain.product.AddProductForm;
import org.example.domain.repository.ProductRepository;
import org.example.exception.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.exception.ErrorCode.NOT_FOUND_PRODUCT;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public Product addProduct(Long sellerId, AddProductForm form){
        return productRepository.save(Product.of(sellerId, form));
    }

    public Product getProduct(Long id) {
        return productRepository.findWithProductItemsById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));
    }
}
