package org.example.domain.repository;

import org.example.domain.model.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductItemRepository extends JpaRepository<ProductItem, Long> {

    Optional<ProductItem> findBySellerIdAndId(Long sellerId, Long id);

}
