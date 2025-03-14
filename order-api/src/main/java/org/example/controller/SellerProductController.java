package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.config.JwtAuthenticationProvider;
import org.example.domain.product.*;
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

    @PutMapping
    public ResponseEntity<ProductDto> updateProduct(
            @RequestHeader(name = "X-AUTH-TOKEN") String token,
            @RequestBody UpdateProductForm form
    ){
        return ResponseEntity.ok(ProductDto.from(productService.updateProduct(provider.getUserVo(token).getId(), form)));
    }

    @PutMapping("/item")
    public ResponseEntity<ProductItemDto> updateProductItem(
            @RequestHeader(name = "X-AUTH-TOKEN") String token,
            @RequestBody UpdateProductItemForm form
    ){
        return ResponseEntity.ok(ProductItemDto.from(productItemService.updateProductItem(provider.getUserVo(token).getId(), form)));
    }

    @DeleteMapping
    public ResponseEntity<ProductDto> deleteProduct(
            @RequestHeader(name = "X-AUTH-TOKEN") String token,
            @RequestParam Long productId
    ){
        return ResponseEntity.ok(ProductDto.from(productService.deleteProduct(provider.getUserVo(token).getId(), productId)));
    }


    @GetMapping("/item")
    public ResponseEntity<ProductDto> addProductItem(
            @RequestParam Long id
    ){
        return ResponseEntity.ok(ProductDto.from(productService.getProduct(id)));
    }

}
