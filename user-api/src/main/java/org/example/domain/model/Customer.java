package org.example.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.SignUpForm;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class) // Customer이 변경될 때, createAt과 modifedAt이 자동으로 update됨!
public class Customer extends BaseEntity{

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email; // 이메일은 유니크하게
    private String name;
    private String password; // password 암호화 필수!
    private String phone;
    private LocalDate birth;

    private LocalDateTime verifyExpiredAt;
    private String verificationCode;
    private boolean verify;

    @Column(columnDefinition = "int default 0")
    private Integer balance;

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public void setVerifyExpiredAt(LocalDateTime verifyExpiredAt) {
        this.verifyExpiredAt = verifyExpiredAt;
    }

    public void setVerify(boolean verify) {
        this.verify = verify;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }
    public static Customer from(SignUpForm form){
        return Customer.builder()
                .email(form.getEmail().toLowerCase(Locale.ROOT))
                .password(form.getPassword())
                .name(form.getName())
                .birth(form.getBirth())
                .phone(form.getPhone()) // 핸드폰 번호 validation 적용하는 것 고민해보기.
                .verify(false)
                .build();
    }



}
