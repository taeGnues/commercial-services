package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.config.JwtAuthenticationProvider;
import org.example.domain.common.UserVo;
import org.example.domain.customer.CustomerDto;
import org.example.domain.customer.SellerDto;
import org.example.domain.model.Customer;
import org.example.domain.model.Seller;
import org.example.exception.CustomException;
import org.example.exception.ErrorCode;
import org.example.service.seller.SellerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seller")
public class SellerController {

    private final JwtAuthenticationProvider provider;
    private final SellerService sellerService;

    @GetMapping("/getInfo")
    public ResponseEntity<SellerDto> getInfo(@RequestHeader(name = "X-AUTH-TOKEN") String token){
        UserVo vo = provider.getUserVo(token);
        Seller seller = sellerService.findByIdAndEmail(vo.getId(), vo.getEmail())
                .orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_USER));
        return ResponseEntity.ok(SellerDto.from(seller));
    }
}
