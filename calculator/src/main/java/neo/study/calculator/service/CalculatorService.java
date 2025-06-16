package neo.study.calculator.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import neo.study.calculator.dto.CreditDto;
import neo.study.calculator.dto.LoanOfferDto;
import neo.study.calculator.dto.LoanStatementRequestDto;
import neo.study.calculator.dto.PaymentScheduleElementDto;
import neo.study.calculator.dto.ScoringDataDto;
import neo.study.calculator.enums.EmploymentStatus;
import neo.study.calculator.utils.CreditCalculationHelper;
import neo.study.calculator.utils.exception.LoanRejectionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalculatorService {

        private final CreditCalculationHelper creditCalculationHelper;


        public List<LoanOfferDto> getPrescoringResults(LoanStatementRequestDto loanStatementData) {
                log.info("Starting the pre-scoring process for the client: {} {}",
                                loanStatementData.getFirstName(), loanStatementData.getLastName());

                log.info("Input data: {}", loanStatementData);

                List<LoanOfferDto> offers = new ArrayList<>();

                offers.add(creditCalculationHelper.createOffer(loanStatementData, false, false));
                offers.add(creditCalculationHelper.createOffer(loanStatementData, false, true));
                offers.add(creditCalculationHelper.createOffer(loanStatementData, true, false));
                offers.add(creditCalculationHelper.createOffer(loanStatementData, true, true));

                offers.stream().forEach(offer -> log.info("Offer result: {}", offer));

                return offers;
        }

        public CreditDto getScoringResult(ScoringDataDto scoringData) {
                log.info("Start scoring result calculation for scoringData: {}", scoringData);

                checkLoanApproval(scoringData);
                log.info("Loan approval check passed");

                BigDecimal rate = creditCalculationHelper.calculateRate(scoringData);
                log.info("Rate calculated successfully: {}", rate);

                BigDecimal totalAmount = creditCalculationHelper.calculateTotalAmount(
                                scoringData.getIsInsuranceEnabled(), scoringData.getAmount());
                log.info("Total amount calculated successfully: {}", totalAmount);

                BigDecimal monthlyPayment = creditCalculationHelper
                                .calculateMonthlyPayment(totalAmount, rate, scoringData.getTerm());
                log.info("Monthly payment calculated successfully: {}", monthlyPayment);

                BigDecimal psk = creditCalculationHelper.calculatePsk(monthlyPayment,
                                scoringData.getTerm(), totalAmount);
                List<PaymentScheduleElementDto> paymentSchedule = creditCalculationHelper
                                .generatePaymentSchedule(totalAmount, rate, scoringData.getTerm());
                log.info("Payment schedule generated successfully");
                paymentSchedule.stream().forEach(ps -> log.info("Payment schedule: {}", ps));

                CreditDto creditDto = CreditDto.builder().amount(totalAmount)
                                .term(scoringData.getTerm()).monthlyPayment(monthlyPayment)
                                .rate(rate).psk(psk)
                                .isInsuranceEnabled(scoringData.getIsInsuranceEnabled())
                                .isSalaryClient(scoringData.getIsSalaryClient())
                                .paymentSchedule(paymentSchedule).build();

                log.debug("Scoring result calculated successfully: {}", creditDto);

                return creditDto;
        }

        /*
         * Рабочий статус: Безработный → отказ
         *
         * Сумма займа больше, чем 24 зарплат → отказ
         *
         * Возраст менее 20 или более 65 лет → отказ
         *
         * Стаж работы: Общий стаж менее 18 месяцев → отказ; Текущий стаж менее 3 месяцев → отказ
         */
        private void checkLoanApproval(ScoringDataDto scoringData) {

                if (scoringData.getEmployment()
                                .getEmploymentStatus() == EmploymentStatus.UNEMPLOYED) {
                        throw new LoanRejectionException("Loan rejected: Client is unemployed");
                }

                BigDecimal maxAllowedAmount = scoringData.getEmployment().getSalary()
                                .multiply(BigDecimal.valueOf(24));
                if (scoringData.getAmount().compareTo(maxAllowedAmount) > 0) {
                        throw new LoanRejectionException(
                                        "Loan rejected: Requested amount exceeds 24 salaries");
                }

                int age = Period.between(scoringData.getBirthdate(), LocalDate.now()).getYears();
                if (age < 20 || age > 65) {
                        throw new LoanRejectionException(
                                        "Loan rejected: Age must be between 20 and 65 years");
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
