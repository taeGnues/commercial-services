package org.example.domain.model;
/*
결제 잔액에 대한 기록을 위한 테이블 (예치금 기록용)
 */

import lombok.*;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
public class CustomerBalanceHistory extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = Customer.class, fetch = FetchType.LAZY)
    private Customer customer;

    private Integer changeMoney;

    private Integer currentMoney;

    private String fromMessage;

    private String description;
}
