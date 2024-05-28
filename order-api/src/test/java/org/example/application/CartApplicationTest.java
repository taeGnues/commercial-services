package org.example.application;

import org.example.domain.model.Product;
import org.example.domain.product.AddProductCartForm;
import org.example.domain.product.AddProductForm;
import org.example.domain.product.AddProductItemForm;
import org.example.domain.repository.ProductRepository;
import org.example.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SpringBootTest
class CartApplicationTest {

    @Autowired
    private CartApplication cartApplication;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @Transactional
    void ADD_AND_REFRESH_TEST(){
        Product p = add_product();
        Product r = productRepository.findWithProductItemsById(p.getId()).get();

        assertNotNull(r);

        // 나머지 필드들의 검증.
        assertEquals(r.getName(), "나이키 에어포스");
        assertEquals(r.getDescription(), "신발입니다.");
        assertEquals(r.getProductItems().size(), 3);
        assertEquals(r.getProductItems().get(0).getName(), "나이키 에어포스0");
        assertEquals(r.getProductItems().get(1).getName(), "나이키 에어포스1");

        Long customerId = 100L;

        AddProductCartForm makeForm(Product p){
            AddProductCartForm.ProductItem.builder()
                    .id(p.getId())
        }

    }

    Product add_product() {
        Long sellerId = 1L;

        AddProductForm form = makeProductForm("나이키 에어포스", "신발입니다.", 3);

        Product p = productService.addProduct(sellerId, form);


        return p;

    }

    private static AddProductForm makeProductForm(String name, String description, int itemCount){
        List<AddProductItemForm> itemForms = new ArrayList<>();

        for (int i = 0 ; i < itemCount; i++){
            itemForms.add(makeProductItemForm(null, name+i));
        }
        return AddProductForm.builder()
                .name(name)
                .description(description)
                .addProductItemForms(itemForms)
                .build();
    }

    private static AddProductItemForm makeProductItemForm(Long productId, String name){
        return AddProductItemForm.builder()
                .productId(productId)
                .name(name)
                .price(10000)
                .count(1)
                .build();

    }


}