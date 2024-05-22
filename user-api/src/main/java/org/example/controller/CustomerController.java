package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.config.JwtAuthenticationProvider;
import org.example.domain.common.UserVo;
import org.example.domain.customer.CustomerDto;
import org.example.domain.model.Customer;
import org.example.exception.CustomException;
import org.example.exception.ErroCode;
import org.example.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerController {

    private final JwtAuthenticationProvider provider;
    private final CustomerService customerService;
    /*
    토큰을 활용해서 유저 정보 가져오기
     */
    @GetMapping("/getInfo")
    public ResponseEntity<CustomerDto> getInfo(@RequestHeader(name = "X-AUTH-TOKEN") String token){
        UserVo vo = provider.getUserVo(token);
        Customer customer = customerService.findByIdAndEmail(vo.getId(), vo.getEmail())
                .orElseThrow(()->new CustomException(ErroCode.NOT_FOUND_USER));
        return ResponseEntity.ok(CustomerDto.from(customer));
    }
}
