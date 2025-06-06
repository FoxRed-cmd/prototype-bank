package neo.study.calculator.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import neo.study.calculator.dto.LoanOfferDto;
import neo.study.calculator.dto.LoanStatementRequestDto;

@Service
public class CalculatorService {
    private static final BigDecimal MONTHS_IN_YEAR = BigDecimal.valueOf(12);
    private static final BigDecimal PERCENT_DIVISOR = BigDecimal.valueOf(100);
    private static final Integer ACCURACY_IN_CALCULATION = 10;
    private static final Integer ACCURACY_IN_RESULT = 2;

    @Value("${loan.base-rate}")
    private double baseRate;

    @Value("${loan.insurance-discount}")
    private double insuranceDiscount;

    @Value("${loan.salary-client-discount}")
    private double salaryClientDiscount;

    @Value("${loan.insurance-cost-percent}")
    private double insuranceCostPercent;

    public List<LoanOfferDto> getPrescoringResults(LoanStatementRequestDto request) {
        List<LoanOfferDto> offers = new ArrayList<>();

        offers.add(createOffer(request, false, false));
        offers.add(createOffer(request, false, true));
        offers.add(createOffer(request, true, false));
        offers.add(createOffer(request, true, true));

        return offers;
    }

    private LoanOfferDto createOffer(LoanStatementRequestDto request, boolean isInsuranceEnabled,
            boolean isSalaryClient) {

        BigDecimal rate = BigDecimal.valueOf(baseRate);
        BigDecimal insuranceCost = BigDecimal.ZERO;

        if (isInsuranceEnabled) {
            insuranceCost =
                    request.getAmount().multiply(BigDecimal.valueOf(insuranceCostPercent / 100));
            rate = rate.subtract(BigDecimal.valueOf(insuranceDiscount));
        }

        if (isSalaryClient) {
            rate = rate.subtract(BigDecimal.valueOf(salaryClientDiscount));
        }

        BigDecimal totalAmount = request.getAmount().add(insuranceCost);

        return LoanOfferDto.builder().statementId(UUID.randomUUID())
                .requestedAmount(request.getAmount()).totalAmount(totalAmount)
                .term(request.getTerm())
                .monthlyPayment(calculateMonthlyPayment(totalAmount, rate, request.getTerm()))
                .rate(rate).isInsuranceEnabled(isInsuranceEnabled).isSalaryClient(isSalaryClient)
                .build();
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal totalAmount, BigDecimal rate,
            Integer term) {

        BigDecimal monthlyRate = rate.divide(MONTHS_IN_YEAR.multiply(PERCENT_DIVISOR),
                ACCURACY_IN_CALCULATION, RoundingMode.UP);
        BigDecimal subCoefficient = monthlyRate.add(BigDecimal.ONE).pow(term)
                .setScale(ACCURACY_IN_CALCULATION, RoundingMode.UP);
        BigDecimal coefficient = totalAmount.multiply((monthlyRate.multiply(subCoefficient)).divide(
                subCoefficient.subtract(BigDecimal.ONE), ACCURACY_IN_CALCULATION, RoundingMode.UP));

        return coefficient.setScale(ACCURACY_IN_RESULT, RoundingMode.UP);
    }
}
