package neo.study.calculator.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import neo.study.calculator.dto.EmploymentDto;
import neo.study.calculator.dto.PaymentScheduleElementDto;
import neo.study.calculator.dto.ScoringDataDto;
import neo.study.calculator.enums.EmploymentStatus;
import neo.study.calculator.enums.Gender;
import neo.study.calculator.enums.MaritalStatus;
import neo.study.calculator.enums.Position;

@ExtendWith(MockitoExtension.class)
public class CreditCalculationHelperTest {

    @InjectMocks
    private CreditCalculationHelper helper;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(helper, "baseRate", 15.0);
        ReflectionTestUtils.setField(helper, "insuranceDiscount", 3.0);
        ReflectionTestUtils.setField(helper, "salaryClientDiscount", 1.0);
        ReflectionTestUtils.setField(helper, "insuranceCostPercent", 5.0);
    }

    @Test
    void testCalculateInsuranceCost_enabled() {
        BigDecimal insurance = helper.calculateInsuranceCost(true, new BigDecimal("100000"));
        assertEquals(new BigDecimal("5000.00"), insurance);
    }

    @Test
    void testCalculateInsuranceCost_disabled() {
        BigDecimal insurance = helper.calculateInsuranceCost(false, new BigDecimal("100000"));
        assertEquals(BigDecimal.ZERO, insurance);
    }

    @Test
    void testCalculateMonthlyPayment() {
        BigDecimal monthly = helper.calculateMonthlyPayment(new BigDecimal("100000"),
                new BigDecimal("10.0"), 12);
        assertEquals(new BigDecimal("8791.59"), monthly);
    }

    @Test
    void testCalculateRate_withDiscounts() {
        BigDecimal rate = helper.calculateRate(true, true);
        assertEquals(new BigDecimal("11.0"), rate); // 15 - 3 - 1
    }

    @Test
    void testCalculateTotalAmount_withInsurance() {
        BigDecimal total = helper.calculateTotalAmount(true, new BigDecimal("100000"));
        assertEquals(new BigDecimal("105000.00"), total);
    }

    @Test
    void testCalculatePsk() {
        BigDecimal psk =
                helper.calculatePsk(new BigDecimal("8791.59"), 12, new BigDecimal("100000"));
        assertEquals(new BigDecimal("5.50"), psk);
    }

    @Test
    void testGeneratePaymentSchedule() {
        List<PaymentScheduleElementDto> schedule = helper
                .generatePaymentSchedule(new BigDecimal("100000"), new BigDecimal("10.0"), 12);

        assertEquals(12, schedule.size());

        PaymentScheduleElementDto first = schedule.get(0);
        assertEquals(1, first.getNumber());
        assertNotNull(first.getDate());
        assertEquals(new BigDecimal("8791.59"), first.getTotalPayment());
    }

    @Test
    void testCalculateRateWithScoringData() {
        ScoringDataDto dto = createScoringData();
        BigDecimal resultRate = helper.calculateRate(dto);

        // 15 + 0.5 (EMPLOYED) - 3 (MARRIED) - 2 (MIDDLE_MANAGER) - 3 (FEMALE AND AGE BETWEEN 32 AND
        // 60) = 7.5
        assertEquals(new BigDecimal("7.5"), resultRate);
    }

    private ScoringDataDto createScoringData() {
        EmploymentDto employment = EmploymentDto.builder()
                .employmentStatus(EmploymentStatus.EMPLOYED).position(Position.MIDDLE_MANAGER)
                .employerINN("1234567890").salary(new BigDecimal("50000")).workExperienceCurrent(12)
                .workExperienceTotal(36).build();

        return ScoringDataDto.builder().firstName("Anna").lastName("Ivanova").gender(Gender.FEMALE)
                .birthdate(LocalDate.now().minusYears(35)).maritalStatus(MaritalStatus.MARRIED)
                .employment(employment).accountNumber("40817810099910004312").passportSeries("1234")
                .passportNumber("567890").passportIssueBranch("OVD")
                .passportIssueDate(LocalDate.now().minusYears(10)).isInsuranceEnabled(false)
                .isSalaryClient(false).dependentAmount(1).amount(new BigDecimal("100000")).term(12)
                .build();
    }
}
