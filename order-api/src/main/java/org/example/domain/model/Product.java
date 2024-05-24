package org.example.domain.model;

import lombok.*;
import org.example.domain.product.AddProductForm;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor // builder 사용 시 noargs, allargs를 다 써줘야함.
@Builder
@Audited // Entity에 대한 정보가 변경될때마다 무조건 모든 필드 정보들을 저장해서, 그러한 정보들을 트래킹할 수 있음.
@AuditOverride(forClass = BaseEntity.class)
public class Product extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long sellerId; // 셀러가 등록
    private String name;
    private String description;

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductItem> productItems = new ArrayList<>();

    public static Product of(Long sellerId, AddProductForm form){
        return Product.builder()
                .sellerId(sellerId)
                .name(form.getName())
                .description(form.getDescription())
                .productItems(form.getAddProductItemForms()
                        .stream()
                        .map(piFrom->ProductItem.of(sellerId, piFrom))
                        .collect(Collectors.toList()))
                .build();
    }

}
