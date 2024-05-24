package org.example.domain.repository;

import org.example.domain.model.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

//    @Query("select p from Product p left join fetch p.productItems") // item값들 즉시 로드해오기.
    @EntityGraph(attributePaths = {"productItems"})
    Optional<Product> findWithProductItemsById(Long id);

    @EntityGraph(attributePaths = {"productItems"})
    Optional<Product> findBySellerIdAndId(Long sellerId, Long id);
}
