package neo.study.deal.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import neo.study.deal.dto.CreditDto;
import neo.study.deal.dto.CreditStatus;
import neo.study.deal.entity.Credit;
import neo.study.deal.repository.CreditRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreditService {
    private final CreditRepository creditRepository;

    /*
     * Создание кредита и сохранение в базе данных
     */
    public Credit create(CreditDto creditDto) {
        log.debug("Start creating credit: {}", creditDto);

        var credit = Credit.builder()
                .amount(creditDto.getAmount())
                .term(creditDto.getTerm())
                .monthlyPayment(creditDto.getMonthlyPayment())
                .rate(creditDto.getRate())
                .psk(creditDto.getPsk())
                .paymentSchedule(creditDto.getPaymentSchedule())
                .insuranceEnabled(creditDto.getIsInsuranceEnabled())
                .salaryClient(creditDto.getIsSalaryClient())
                .status(CreditStatus.CALCULATED)
                .build();

        log.debug("Created credit and saved in DB: {}", credit);

        return creditRepository.save(credit);
    }
}
