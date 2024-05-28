package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.model.Product;
import org.example.domain.model.ProductItem;
import org.example.domain.product.AddProductItemForm;
import org.example.domain.product.UpdateProductItemForm;
import org.example.domain.repository.ProductItemRepository;
import org.example.domain.repository.ProductRepository;
import org.example.exception.CustomException;
import org.example.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ProductItemService {
    private final ProductRepository productRepository;
    private final ProductItemRepository productItemRepository;

    public ProductItem getProductItem(Long id){
        return productItemRepository.getById(id);
    }

    public ProductItem saveProductItem(ProductItem productItem){
        return productItemRepository.save(productItem);
    }

    @Transactional
    public Product addProductItem(Long sellerId, AddProductItemForm form){
        Product product = productRepository.findByIdAndSellerId(form.getProductId(), sellerId)
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

    @Transactional
    public ProductItem updateProductItem(Long sellerId, UpdateProductItemForm form){
        ProductItem productItem = productItemRepository.findBySellerIdAndId(sellerId, form.getId())
                .orElseThrow(()->new CustomException(NOT_FOUND_ITEM));
        productItem.setName(form.getName());
        productItem.setCount(form.getCount());
        productItem.setPrice(form.getPrice());
        return productItem;
    }
}
