package org.example.service.customer;

import lombok.RequiredArgsConstructor;
import org.example.domain.customer.ChangeBalanceForm;
import org.example.domain.model.CustomerBalanceHistory;
import org.example.domain.repository.CustomerBalanceHistoryRepository;
import org.example.domain.repository.CustomerRepository;
import org.example.exception.CustomException;
import org.example.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerBalanceService {

    private final CustomerBalanceHistoryRepository customerBalanceHistoryRepository;
    private final CustomerRepository customerRepository;

    /*
    잔액 변경 (입금 or 출금)
     */
    @Transactional(noRollbackFor = {CustomException.class}) // 해당 exception이 발생할 경우, no rollback 한다는 의미.
    public CustomerBalanceHistory changeBalance(Long customerId, ChangeBalanceForm form) throws CustomException {

        // 1. customer Id로 히스토리 조회
        CustomerBalanceHistory customerBalanceHistory = customerBalanceHistoryRepository.findFirstByCustomer_IdOrderByIdDesc(customerId)
                .orElse( // balance history가 없다는 말은 돈이 현재 0이라는 뜻.
                        CustomerBalanceHistory.builder()
                                .changeMoney(0)
                                .currentMoney(0)
                                .customer(customerRepository.findById(customerId)
                                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER)))
                                .build()
                );


        // 만약 돈이 부족시.
        if(customerBalanceHistory.getCurrentMoney() + form.getMoney() < 0){ // 마이너스 통장이 될 때
            throw new CustomException(ErrorCode.NOT_ENOUGH_BALANCE);
        }

        // 잔고 기록.
        customerBalanceHistory = CustomerBalanceHistory.builder()
                .changeMoney(form.getMoney())
                .currentMoney(customerBalanceHistory.getCurrentMoney() + form.getMoney())
                .description(form.getMessage())
                .fromMessage(form.getFrom())
                .customer(customerBalanceHistory.getCustomer())
                .build();

        // 해당 customer의 balance도 설정해줌.
        customerBalanceHistory.getCustomer().setBalance(customerBalanceHistory.getCurrentMoney());

        customerBalanceHistoryRepository.save(customerBalanceHistory);

        return customerBalanceHistory;
    }
}
