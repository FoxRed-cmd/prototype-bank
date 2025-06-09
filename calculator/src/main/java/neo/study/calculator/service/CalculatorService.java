package neo.study.calculator.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import neo.study.calculator.dto.CreditDto;
import neo.study.calculator.dto.LoanOfferDto;
import neo.study.calculator.dto.LoanStatementRequestDto;
import neo.study.calculator.dto.PaymentScheduleElementDto;
import neo.study.calculator.dto.ScoringDataDto;
import neo.study.calculator.enums.EmploymentStatus;
import neo.study.calculator.utils.CreditCalculationHelper;
import neo.study.calculator.utils.exception.LoanRejectionException;

@Service
@RequiredArgsConstructor
public class CalculatorService {

    private final CreditCalculationHelper creditCalculationHelper;


    public List<LoanOfferDto> getPrescoringResults(LoanStatementRequestDto loanStatementData) {

        List<LoanOfferDto> offers = new ArrayList<>();

        offers.add(creditCalculationHelper.createOffer(loanStatementData, false, false));
        offers.add(creditCalculationHelper.createOffer(loanStatementData, false, true));
        offers.add(creditCalculationHelper.createOffer(loanStatementData, true, false));
        offers.add(creditCalculationHelper.createOffer(loanStatementData, true, true));

        return offers;
    }

    public CreditDto getScoringResult(ScoringDataDto scoringData) {

        checkLoanApproval(scoringData);

        BigDecimal rate = creditCalculationHelper.calculateRate(scoringData);
        BigDecimal totalAmount = creditCalculationHelper
                .calculateTotalAmount(scoringData.getIsInsuranceEnabled(), scoringData.getAmount());
        BigDecimal monthlyPayment = creditCalculationHelper.calculateMonthlyPayment(totalAmount,
                rate, scoringData.getTerm());
        BigDecimal psk = creditCalculationHelper.calculatePsk(monthlyPayment, scoringData.getTerm(),
                totalAmount);
        List<PaymentScheduleElementDto> paymentSchedule = creditCalculationHelper
                .generatePaymentSchedule(totalAmount, rate, scoringData.getTerm());

        return CreditDto.builder().amount(totalAmount).term(scoringData.getTerm())
                .monthlyPayment(monthlyPayment).rate(rate).psk(psk)
                .isInsuranceEnabled(scoringData.getIsInsuranceEnabled())
                .isSalaryClient(scoringData.getIsSalaryClient()).paymentSchedule(paymentSchedule)
                .build();
    }

    private void checkLoanApproval(ScoringDataDto scoringData) {

        if (scoringData.getEmployment().getEmploymentStatus() == EmploymentStatus.UNEMPLOYED) {
            throw new LoanRejectionException("Loan rejected: Client is unemployed");
        }

        BigDecimal maxAllowedAmount =
                scoringData.getEmployment().getSalary().multiply(BigDecimal.valueOf(24));
        if (scoringData.getAmount().compareTo(maxAllowedAmount) > 0) {
            throw new LoanRejectionException("Loan rejected: Requested amount exceeds 24 salaries");
        }

        int age = Period.between(scoringData.getBirthdate(), LocalDate.now()).getYears();
        if (age < 20 || age > 65) {
            throw new LoanRejectionException("Loan rejected: Age must be between 20 and 65 years");
        }

        if (scoringData.getEmployment().getWorkExperienceTotal() < 18) {
            throw new LoanRejectionException(
                    "Loan rejected: Total work experience less than 18 months");
        }

        if (scoringData.getEmployment().getWorkExperienceCurrent() < 3) {
            throw new LoanRejectionException(
                    "Loan rejected: Current work experience less than 3 months");
        }
    }
}
