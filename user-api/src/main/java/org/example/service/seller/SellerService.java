package org.example.service.seller;

import lombok.RequiredArgsConstructor;
import org.example.domain.SignUpForm;
import org.example.domain.model.Seller;
import org.example.domain.repository.SellerRepository;
import org.example.exception.CustomException;
import org.example.exception.ErrorCode;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SellerService {
    private final SellerRepository sellerRepository;

    public Optional<Seller> findByIdAndEmail(Long id, String email){
        return sellerRepository.findByIdAndEmail(id, email);
    }

    public Optional<Seller> findValidSeller(String email, String password){
        return sellerRepository.findByEmailAndPasswordAndVerifyIsTrue(email, password);
    }

    public Seller signUp(SignUpForm form){
        return sellerRepository.save(Seller.from(form));
    }

    public boolean isEmailExist(String email){
        return sellerRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public void verifyEmail(String email, String code){
        Seller seller = sellerRepository.findByEmail(email)
                .orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_USER));

        if(seller.isVerify()){
            throw new CustomException(ErrorCode.ALREADY_VERIFY);
        }else if (!seller.getVerificationCode().equals(code)){
            throw new CustomException(ErrorCode.WRONG_VERIFICATION);
        }else if(seller.getVerifyExpiredAt().isBefore(LocalDateTime.now())){ // expire가 now()보다 더 과거인가요?
            throw new CustomException(ErrorCode.EXPIRED_CODE);
        }

        seller.setVerify(true);
    }

    @Transactional
    public LocalDateTime changeSellerValidateEmail(Long sellerId, String verificationCode){
        Optional<Seller> sellerOptional = sellerRepository.findById(sellerId);

        if(sellerOptional.isPresent()){
            Seller seller = sellerOptional.get();
            seller.setVerificationCode(verificationCode);
            seller.setVerifyExpiredAt(LocalDateTime.now().plusDays(1));
            return seller.getVerifyExpiredAt();
        }

        throw new CustomException(ErrorCode.NOT_FOUND_USER);
    }

}
