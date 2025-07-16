package neo.study.calculator.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import neo.study.calculator.dto.LoanOfferDto;
import neo.study.calculator.dto.LoanStatementRequestDto;
import neo.study.calculator.dto.PaymentScheduleElementDto;
import neo.study.calculator.dto.ScoringDataDto;
import neo.study.calculator.enums.Gender;

@Slf4j
@Component
public class CreditCalculationHelper {
    private static final BigDecimal MONTHS_IN_YEAR = BigDecimal.valueOf(12);
    private static final BigDecimal PERCENT_DIVISOR = BigDecimal.valueOf(100);

    private static final BigDecimal INCREASE_FOR_SELF_EMPLOYED = BigDecimal.valueOf(2.0);
    private static final BigDecimal INCREASE_FOR_BUSINESS_OWNER = BigDecimal.valueOf(1.0);
    private static final BigDecimal INCREASE_FOR_EMPLOYED = BigDecimal.valueOf(0.5);
    private static final BigDecimal DECREASE_MIDDLE_MANAGER = BigDecimal.valueOf(2.0);
    private static final BigDecimal DECREASE_FOR_TOP_MANAGER = BigDecimal.valueOf(3.0);
    private static final BigDecimal DECREASE_FOR_MARRIED = BigDecimal.valueOf(3.0);
    private static final BigDecimal INCREASE_FOR_DIVORCED = BigDecimal.valueOf(1.0);
    private static final BigDecimal DECREASE_FOR_FEMALE = BigDecimal.valueOf(3.0);
    private static final BigDecimal DECREASE_FOR_MALE = BigDecimal.valueOf(3.0);

    private static final int MIN_AGE_FOR_MALE = 30;
    private static final int MAX_AGE_FOR_MALE = 55;
    private static final int MIN_AGE_FOR_FEMALE = 32;
    private static final int MAX_AGE_FOR_FEMALE = 60;

    private static final Integer ACCURACY_IN_CALCULATION = 10;
    private static final Integer ACCURACY_IN_RESULT = 2;

    @Value("${loan.base-rate}")
    private String baseRate;

    @Value("${loan.insurance-discount}")
    private String insuranceDiscount;

    @Value("${loan.salary-client-discount}")
    private String salaryClientDiscount;

    @Value("${loan.insurance-cost-percent}")
    private String insuranceCostPercent;

    public LoanOfferDto createOffer(LoanStatementRequestDto request, boolean isInsuranceEnabled,
            boolean isSalaryClient) {
        log.debug(
                "Starting offer creation with input values: {}, isInsuranceEnabled={}, isSalaryClient={}",
                request, isInsuranceEnabled, isSalaryClient);

        BigDecimal rate = new BigDecimal(baseRate);
        rate = calculateRate(rate, isInsuranceEnabled, isSalaryClient);
        log.debug("Possible rate: {}", rate);

        BigDecimal totalAmount = calculateTotalAmount(isInsuranceEnabled, request.getAmount());
        log.debug("Total amount: {}", totalAmount);

        return LoanOfferDto.builder()
                .statementId(UUID.randomUUID())
                .requestedAmount(request.getAmount())
                .totalAmount(totalAmount)
                .term(request.getTerm())
                .monthlyPayment(calculateMonthlyPayment(totalAmount, rate, request.getTerm()))
                .rate(rate)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .build();
    }

    /*
     * Расчет ежемесячного платежа
     *
     * monthlyPayment = totalAmount * (monthlyRate * subCoefficient) /
     * (subCoefficient - 1)
     * subCoefficient = (1 + monthlyRate)^term
     *
     * Где: monthlyPayment - ежемесячный платеж, totalAmount - сумма кредита,
     * monthlyRate -
     * месячная процентная ставка (rate / 12 * 100), subCoefficient - переменная для
     * хранения
     * промежуточного результата, rate - количество месяцев (срок кредитования).
     */
    public BigDecimal calculateMonthlyPayment(BigDecimal totalAmount, BigDecimal rate,
            Integer term) {

        log.debug("Start calculating monthly payment: totalAmount={}, rate={}, term={}", totalAmount,
                rate, term);

        BigDecimal monthlyRate = rate
                .divide(MONTHS_IN_YEAR
                        .multiply(PERCENT_DIVISOR),
                        ACCURACY_IN_CALCULATION, RoundingMode.UP);
        log.debug("Monthly interest rate: {}", monthlyRate);

        BigDecimal subCoefficient = monthlyRate
                .add(BigDecimal.ONE)
                .pow(term)
                .setScale(ACCURACY_IN_CALCULATION, RoundingMode.UP);
        log.debug("Sub coefficient: {}", subCoefficient);

        BigDecimal monthlyPayment = (totalAmount
                .multiply((monthlyRate
                        .multiply(subCoefficient))
                        .divide(subCoefficient
                                .subtract(BigDecimal.ONE), ACCURACY_IN_CALCULATION,
                                RoundingMode.UP)))
                .setScale(ACCURACY_IN_RESULT, RoundingMode.UP);

        log.debug("Monthly payment result: {}", monthlyPayment);

        return monthlyPayment;
    }

    /*
     * Расчет ставки с учетом страховки и ставки для зарплатных клиентов
     *
     * Если страховка включена, то уменьшаем ставку на 3%
     *
     * Если клиент зарплатный, то уменьшаем ставку на 1%
     */

    public BigDecimal calculateRate(BigDecimal rate, boolean isInsuranceEnabled,
            boolean isSalaryClient) {

        log.debug("Calculating base rate: baseRate={}, isInsuranceEnabled={}, isSalaryClient={}",
                rate, isInsuranceEnabled, isSalaryClient);

        if (isInsuranceEnabled) {
            rate = rate.subtract(new BigDecimal(insuranceDiscount));
            log.debug("Applied insurance discount: {}", insuranceDiscount);
        }

        if (isSalaryClient) {
            rate = rate.subtract(new BigDecimal(salaryClientDiscount));
            log.debug("Applied salary client discount: {}", salaryClientDiscount);
        }

        log.debug("Final rate after basic discounts: {}", rate);
        return rate;
    }

    /*
     * Расчет ставки с учетом всех правил скоринга
     *
     * Рабочий статус: Самозанятый → ставка увеличивается на 2; Владелец бизнеса →
     * ставка
     * увеличивается на 1; Работающий → ставка увеличивается на 0.5
     *
     * Позиция на работе: Менеджер среднего звена → ставка уменьшается на 2;
     * Топ-менеджер → ставка
     * уменьшается на 3
     *
     * Семейное положение: Замужем/женат → ставка уменьшается на 3; Разведен,
     * холост, овдовел →
     * ставка увеличивается на 1
     *
     * Пол: Женщина, возраст от 32 до 60 лет → ставка уменьшается на 3; Мужчина,
     * возраст от 30 до 55
     * лет → ставка уменьшается на 3;
     *
     * Ставка не может быть меньше 5%
     */

    public BigDecimal calculateRate(ScoringDataDto scoringData) {
        log.info("Calculating full rate for scoringData={}", scoringData);
        BigDecimal rate = calculateRate(new BigDecimal(baseRate),
                scoringData.getIsInsuranceEnabled(), scoringData.getIsSalaryClient());
        log.debug("Base rate after initial discounts: {}", rate);

        rate = switch (scoringData.getEmployment().getEmploymentStatus()) {
            case SELF_EMPLOYED -> {
                log.debug("Employment status: SELF_EMPLOYED, applying increase: {}",
                        INCREASE_FOR_SELF_EMPLOYED);
                yield rate.add(INCREASE_FOR_SELF_EMPLOYED);
            }
            case BUSINESS_OWNER -> {
                log.debug("Employment status: BUSINESS_OWNER, applying increase: {}",
                        INCREASE_FOR_BUSINESS_OWNER);
                yield rate.add(INCREASE_FOR_BUSINESS_OWNER);
            }
            case EMPLOYED -> {
                log.debug("Employment status: EMPLOYED, applying increase: {}",
                        INCREASE_FOR_EMPLOYED);
                yield rate.add(INCREASE_FOR_EMPLOYED);
            }
            default -> rate;
        };

        rate = switch (scoringData.getEmployment().getPosition()) {
            case MIDDLE_MANAGER -> {
                log.debug("Position: MIDDLE_MANAGER, applying decrease: {}",
                        DECREASE_MIDDLE_MANAGER);
                yield rate.subtract(DECREASE_MIDDLE_MANAGER);
            }
            case TOP_MANAGER -> {
                log.debug("Position: TOP_MANAGER, applying decrease: {}", DECREASE_FOR_TOP_MANAGER);
                yield rate.subtract(DECREASE_FOR_TOP_MANAGER);
            }
            default -> rate;
        };

        rate = switch (scoringData.getMaritalStatus()) {
            case MARRIED -> {
                log.debug("Marital status: MARRIED, applying decrease: {}", DECREASE_FOR_MARRIED);
                yield rate.subtract(DECREASE_FOR_MARRIED);
            }
            case DIVORCED, SINGLE, WIDOWED -> {
                log.debug("Marital status: DIVORCED, applying increase: {}", INCREASE_FOR_DIVORCED);
                yield rate.add(INCREASE_FOR_DIVORCED);
            }
            default -> rate;
        };

        int age = Period.between(scoringData.getBirthdate(), LocalDate.now()).getYears();
        log.debug("Client age: {}", age);

        if (scoringData.getGender() == Gender.FEMALE && age >= MIN_AGE_FOR_FEMALE
                && age <= MAX_AGE_FOR_FEMALE) {
            log.debug("Gender: FEMALE within range, applying decrease: {}", DECREASE_FOR_FEMALE);
            rate = rate.subtract(DECREASE_FOR_FEMALE);
        } else if (scoringData.getGender() == Gender.MALE && age >= MIN_AGE_FOR_MALE
                && age <= MAX_AGE_FOR_MALE) {
            log.debug("Gender: MALE within range, applying decrease: {}", DECREASE_FOR_MALE);
            rate = rate.subtract(DECREASE_FOR_MALE);
        }

        BigDecimal finalRate = rate.max(BigDecimal.valueOf(5.0));
        log.info("Final calculated rate: {}", finalRate);
        return finalRate;
    }

    public BigDecimal calculateTotalAmount(boolean isInsuranceEnabled, BigDecimal amount) {
        log.debug("Calculating total amount: isInsuranceEnabled={}, amount={}", isInsuranceEnabled,
                amount);

        BigDecimal insuranceCost = calculateInsuranceCost(isInsuranceEnabled, amount);
        BigDecimal total = amount.add(insuranceCost);

        log.debug("Insurance cost: {}, Total amount: {}", insuranceCost, total);
        return total;
    }

    /*
     * Расчет стоимости страхования
     *
     * insuranceCost = amount * (insuranceCostPercent / 100)
     *
     * Где: insuranceCost - стоимость страхования, amount - сумма кредита,
     * insuranceCostPercent -
     * процент стоимости страхования
     *
     */
    public BigDecimal calculateInsuranceCost(boolean isInsuranceEnabled, BigDecimal amount) {

        log.debug("Calculating insurance cost: isInsuranceEnabled={}, amount={}", isInsuranceEnabled,
                amount);

        if (!isInsuranceEnabled) {
            log.debug("Insurance is not enabled, cost is 0");
            return BigDecimal.ZERO;
        }

        BigDecimal insuranceCost = amount.multiply(BigDecimal
                .valueOf(Double.parseDouble(insuranceCostPercent) / PERCENT_DIVISOR.intValue()));
        log.debug("Insurance cost calculated: {}", insuranceCost);
        return insuranceCost;
    }

    /*
     * Расчет ПСК
     *
     * psk = (totalPayments - totalAmount) / totalAmount * (12 / term) * 100%
     *
     * Где: psk - полная стоимость кредита за год в процентах, totalPayments - общая
     * сумма
     * платежей, totalAmount - сумма кредита, term - количество месяцев
     */
    public BigDecimal calculatePsk(BigDecimal monthlyPayment, Integer term,
            BigDecimal totalAmount) {
        log.debug("Calculating PSK: monthlyPayment={}, term={}, totalAmount={}", monthlyPayment,
                term, totalAmount);
        BigDecimal totalPayments = monthlyPayment.multiply(BigDecimal.valueOf(term));
        log.debug("Total payments: {}", totalPayments);

        BigDecimal psk = totalPayments.subtract(totalAmount)
                .divide(totalAmount, ACCURACY_IN_CALCULATION, RoundingMode.UP)
                .multiply(MONTHS_IN_YEAR)
                .divide(BigDecimal.valueOf(term), ACCURACY_IN_CALCULATION, RoundingMode.UP)
                .multiply(PERCENT_DIVISOR);

        BigDecimal result = psk.setScale(ACCURACY_IN_RESULT, RoundingMode.UP);
        log.debug("PSK result: {}", result);
        return result;
    }

    /*
     * Генерация расписания платежей
     *
     * Расчет начисленных процентов
     *
     * interestPayment = remainingDebt * (rate / 12 * 100)
     *
     * remainingDebt = remainingDebt - debtPayment
     *
     * Где: rate - количество месяцев (срок кредитования), remainingDebt - остаток
     * долга
     *
     *
     * Расчет оплаты долга
     *
     * debtPayment = monthlyPayment - interestPayment
     *
     * Где: debtPayment - оплата долга, monthlyPayment - ежемесячный платеж,
     * interestPayment
     * начисленные проценты
     */

    public List<PaymentScheduleElementDto> generatePaymentSchedule(BigDecimal totalAmount,
            BigDecimal rate, Integer term) {

        log.info("Generating payment schedule: totalAmount={}, rate={}, term={}", totalAmount, rate,
                term);
        List<PaymentScheduleElementDto> schedule = new ArrayList<>();
        BigDecimal remainingDebt = totalAmount;
        BigDecimal monthlyRate = rate
                .divide(MONTHS_IN_YEAR
                        .multiply(PERCENT_DIVISOR),
                        ACCURACY_IN_CALCULATION, RoundingMode.UP);
        BigDecimal monthlyPayment = calculateMonthlyPayment(totalAmount, rate, term);
        LocalDate currentDate = LocalDate.now().plusMonths(1);

        for (int i = 1; i <= term; i++) {
            BigDecimal interestPayment = remainingDebt
                    .multiply(monthlyRate)
                    .setScale(ACCURACY_IN_RESULT, RoundingMode.UP);
            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment);

            PaymentScheduleElementDto element = PaymentScheduleElementDto.builder()
                    .number(i)
                    .date(currentDate)
                    .totalPayment(monthlyPayment)
                    .interestPayment(interestPayment)
                    .debtPayment(debtPayment)
                    .remainingDebt(remainingDebt)
                    .build();

            log.debug("Schedule element {}: {}", i, element);

            schedule.add(element);
            remainingDebt = remainingDebt.subtract(debtPayment);
            currentDate = currentDate.plusMonths(1);
        }

        log.info("Payment schedule generation complete with {} elements", schedule.size());
        return schedule;
    }
}
