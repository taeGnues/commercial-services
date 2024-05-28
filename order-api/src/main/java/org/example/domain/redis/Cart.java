package org.example.domain.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.domain.model.ProductItem;
import org.example.domain.product.AddProductCartForm;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@RedisHash("cart")
public class Cart {
    @Id
    private Long customerId;
    private List<Product> products = new ArrayList<>();
    private List<String> messages = new ArrayList<>(); // 메세지 함.

    public void addMessage(String message){
        messages.add(message);
    }

    public Cart(Long customerId){
        this.customerId = customerId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Product{
        private Long id;
        private Long sellerId;
        private String name;
        private String descripton;
        private List<ProductItem> items = new ArrayList<>();

        public static Product from(AddProductCartForm form){
            return Product.builder()
                    .id(form.getProductId())
                    .sellerId(form.getSellerId())
                    .name(form.getName())
                    .descripton(form.getDescription())
                    .items(form.getItems().stream()
                            .map(m -> ProductItem.from(m)).collect(Collectors.toList()))
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductItem{
        private Long id;
        private String name;
        private Integer count;
        private Integer price;

        public static ProductItem from(AddProductCartForm.ProductItem m) {
            return ProductItem.builder()
                    .id(m.getId())
                    .name(m.getName())
                    .count(m.getCount())
                    .price(m.getPrice())
                    .build();
        }
    }

}
