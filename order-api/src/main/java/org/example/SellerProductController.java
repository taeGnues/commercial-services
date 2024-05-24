package org.example;

import lombok.RequiredArgsConstructor;
import org.example.config.JwtAuthenticationProvider;
import org.example.domain.product.AddProductForm;
import org.example.domain.product.AddProductItemForm;
import org.example.domain.product.ProductDto;
import org.example.domain.product.ProductItemDto;
import org.example.service.ProductItemService;
import org.example.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seller/product")
public class SellerProductController {

    private final ProductService productService;
    private final ProductItemService productItemService;
    private final JwtAuthenticationProvider provider;

    @PostMapping
    public ResponseEntity<ProductDto> addProduct(
            @RequestHeader(name = "X-AUTH-TOKEN") String token,
            @RequestBody AddProductForm form
    ){
        return ResponseEntity.ok(ProductDto.from(productService.addProduct(provider.getUserVo(token).getId(), form)));
    }

    @PostMapping("/item")
    public ResponseEntity<ProductDto> addProductItem(
            @RequestHeader(name = "X-AUTH-TOKEN") String token,
            @RequestBody AddProductItemForm form
    ){
        return ResponseEntity.ok(ProductDto.from(productItemService.addProductItem(provider.getUserVo(token).getId(), form)));
    }

    @GetMapping("/item")
    public ResponseEntity<ProductDto> addProductItem(
            @RequestParam Long id
    ){
        return ResponseEntity.ok(ProductDto.from(productService.getProduct(id)));
    }
}
