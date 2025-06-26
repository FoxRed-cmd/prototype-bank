package neo.study.deal.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import neo.study.deal.dto.CreditDto;
import neo.study.deal.dto.CreditStatus;
import neo.study.deal.entity.Credit;
import neo.study.deal.repository.CreditRepository;

@Service
@RequiredArgsConstructor
public class CreditService {
    private final CreditRepository creditRepository;

    /*
     * Создание кредита и сохранение в базе данных
     */
    public Credit create(CreditDto creditDto) {
        var credit = Credit.builder().amount(creditDto.getAmount()).term(creditDto.getTerm())
                .monthlyPayment(creditDto.getMonthlyPayment()).rate(creditDto.getRate())
                .psk(creditDto.getPsk()).paymentSchedule(creditDto.getPaymentSchedule())
                .insuranceEnabled(creditDto.getIsInsuranceEnabled())
                .salaryClient(creditDto.getIsSalaryClient()).status(CreditStatus.CALCULATED)
                .build();

        return creditRepository.save(credit);
    }
}
