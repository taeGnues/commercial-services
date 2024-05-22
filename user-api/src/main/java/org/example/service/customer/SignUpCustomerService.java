package org.example.service.customer;

import lombok.RequiredArgsConstructor;
import org.example.domain.SignUpForm;
import org.example.domain.model.Customer;
import org.example.domain.repository.CustomerRepository;
import org.example.exception.CustomException;
import org.example.exception.ErrorCode;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SignUpCustomerService {

    private final CustomerRepository customerRepository;

    public Customer signUp(SignUpForm form){
        return customerRepository.save(Customer.from(form));
    }

    /**
     * 이메일 존재 여부 확인.
     * @param email
     * @return
     */
    public boolean isEmailExist(String email){
        return customerRepository.findByEmail(email.toLowerCase(Locale.ROOT)).isPresent();
    }

    @Transactional
    public LocalDateTime changeCustomerValidateEmail(Long customerId, String verificationCode){
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if(customerOptional.isPresent()){
            Customer customer = customerOptional.get();
            customer.setVerificationCode(verificationCode); // 코드 설정.
            customer.setVerifyExpiredAt(LocalDateTime.now().plusDays(1));
            // 변경감지로 자동으로 저장됨.
            return customer.getVerifyExpiredAt();
        }
        throw new CustomException(ErrorCode.NOT_FOUND_USER);
    }

    @Transactional
    public void verifyEmail(String email, String code){
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_USER));

        if(customer.isVerify()){
            throw new CustomException(ErrorCode.ALREADY_VERIFY);
        }else if(!customer.getVerificationCode().equals(code)){ // 인증 code가 다르면
            throw new CustomException(ErrorCode.WRONG_VERIFICATION);
        }else if(customer.getVerifyExpiredAt().isBefore(LocalDateTime.now())){ // 인증 시간이 지난 경우
            throw new CustomException(ErrorCode.EXPIRED_CODE);
        }

        customer.setVerify(true);
    }
}
