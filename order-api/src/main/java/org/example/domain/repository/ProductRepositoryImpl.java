package org.example.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.domain.model.Product;
import org.example.domain.model.QProduct;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Product> searchByName(String name) {
        String search = "%"+name+"%";
        QProduct qProduct = QProduct.product;
        return queryFactory.selectFrom(qProduct)
                .where(qProduct.name.like(search))
                .fetch();
    }
}
