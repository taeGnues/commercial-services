package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.model.Product;
import org.example.domain.model.ProductItem;
import org.example.domain.product.AddProductForm;
import org.example.domain.product.UpdateProductForm;
import org.example.domain.product.UpdateProductItemForm;
import org.example.domain.repository.ProductRepository;
import org.example.exception.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.exception.ErrorCode.NOT_FOUND_ITEM;
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

    @Transactional
    public Product updateProduct(Long sellerId, UpdateProductForm form){
        Product product = productRepository.findByIdAndSellerId(form.getId(), sellerId)
                .orElseThrow(()->new CustomException(NOT_FOUND_PRODUCT));
        product.setName(form.getName());
        product.setDescription(form.getDescription());

        for(UpdateProductItemForm itemForm : form.getItems()){ // 한 트랜잭션 내에서 처리하기 위해서 이렇게.
            ProductItem item = product.getProductItems().stream() // id와 같은 값들을 모두 바꿔줌!!
                    .filter(pi -> pi.getId().equals(itemForm.getId()))
                    .findFirst().orElseThrow(() -> new CustomException(NOT_FOUND_ITEM));

            item.setName(itemForm.getName());
            item.setPrice(itemForm.getPrice());
            item.setCount(itemForm.getCount());
        }


        return product;
    }
}
