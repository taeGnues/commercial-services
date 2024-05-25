package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.model.Product;
import org.example.domain.repository.ProductItemRepository;
import org.example.domain.repository.ProductRepository;
import org.example.exception.CustomException;
import org.example.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductSearchService {

    private final ProductRepository productRepository;
    private final ProductItemRepository productItemRepository;

    // 검색을 해서 가져옴. -> 보통은 검색 엔진을 사용하기는 함.
    public List<Product> searchByName(String name){
        return productRepository.searchByName(name);
    }

    public Product getByProductId(Long productId){
        return productRepository.findWithProductItemsById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));
    }

    public List<Product> getListByProductIds(List<Long> productIds){
        return productRepository.findAllById(productIds);
    }



}
