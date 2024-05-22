package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.config.JwtAuthenticationProvider;
import org.example.domain.common.UserVo;
import org.example.domain.customer.ChangeBalanceForm;
import org.example.domain.customer.CustomerDto;
import org.example.domain.model.Customer;
import org.example.exception.CustomException;
import org.example.exception.ErrorCode;
import org.example.service.customer.CustomerBalanceService;
import org.example.service.customer.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerController {

    private final JwtAuthenticationProvider provider;
    private final CustomerService customerService;
    private final CustomerBalanceService customerBalanceService;
    /*
    토큰을 활용해서 유저 정보 가져오기
     */
    @GetMapping("/getInfo")
    public ResponseEntity<CustomerDto> getInfo(@RequestHeader(name = "X-AUTH-TOKEN") String token){
        UserVo vo = provider.getUserVo(token);
        Customer customer = customerService.findByIdAndEmail(vo.getId(), vo.getEmail())
                .orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_USER));
        return ResponseEntity.ok(CustomerDto.from(customer));
    }

    /*
    잔액
     */
    @PostMapping("/balance")
    public ResponseEntity<Integer> changeBalance(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                                  @RequestBody ChangeBalanceForm form){
        UserVo vo = provider.getUserVo(token);

        return ResponseEntity.ok(customerBalanceService.changeBalance(vo.getId(), form).getCurrentMoney());
    }
}
