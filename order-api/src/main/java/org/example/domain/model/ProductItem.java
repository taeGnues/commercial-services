package org.example.domain.model;

import lombok.*;
import org.example.domain.product.AddProductItemForm;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditOverride(forClass = BaseEntity.class)
public class ProductItem extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sellerId;

    @Audited
    private String name; // 바뀔 수 있는 정보에 대해서만 audited 하기

    @Audited
    private Integer price; // 바뀔 수 있는 정보에 대해서만 audited 하기

    private Integer count; // 최대 재고 수량을 의미.

    @ManyToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "product_id")
    private Product product;

    // arg가 하나인 경우는 from, arg가 2개 이상인 경우는 of를 사용함.
    public static ProductItem of(Long sellerId, AddProductItemForm form){
        return ProductItem.builder()
                .sellerId(sellerId)
                .name(form.getName())
                .price(form.getPrice())
                .count(form.getCount())
                .build();
    }

}
