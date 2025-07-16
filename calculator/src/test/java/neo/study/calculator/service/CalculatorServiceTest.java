package neo.study.calculator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import neo.study.calculator.dto.CreditDto;
import neo.study.calculator.dto.EmploymentDto;
import neo.study.calculator.dto.LoanOfferDto;
import neo.study.calculator.dto.LoanStatementRequestDto;
import neo.study.calculator.dto.ScoringDataDto;
import neo.study.calculator.enums.EmploymentStatus;
import neo.study.calculator.enums.Gender;
import neo.study.calculator.enums.MaritalStatus;
import neo.study.calculator.enums.Position;
import neo.study.calculator.utils.CreditCalculationHelper;
import neo.study.calculator.utils.exception.NotValidException;

public class CalculatorServiceTest {

    private CreditCalculationHelper helper;

    private CalculatorService calculatorService;

    @BeforeEach
    void init() {
        helper = new CreditCalculationHelper();
        calculatorService = new CalculatorService(helper);
        ReflectionTestUtils.setField(helper, "baseRate", "15.0");
        ReflectionTestUtils.setField(helper, "insuranceDiscount", "3.0");
        ReflectionTestUtils.setField(helper, "salaryClientDiscount", "1.0");
        ReflectionTestUtils.setField(helper, "insuranceCostPercent", "5.0");
    }

    @Test
    void testGetPrescoringResults_Valid() {
        List<LoanOfferDto> offers = calculatorService.getPrescoringResults(getValidLoanStatementRequestDto());
        assertEquals(4, offers.size());
    }

    @Test
    void testGetPrescoringResults_NotValid() {
        NotValidException exception = assertThrows(NotValidException.class,
                () -> calculatorService.getPrescoringResults(getNotValidLoanStatementRequestDto()));
        assertEquals(exception.getExceptions().size(), 9);
        assertTrue(exception.getExceptions().contains("Loan amount must be at least 20000"));
    }

    @Test
    void testGetScoringResult_Valid() {
        CreditDto creditDto = calculatorService.getScoringResult(getValidScoringData());
        assertEquals(creditDto.getPaymentSchedule().size(), 12);
    }

    @Test
    void testGetScoringResult_NotValid() {
        NotValidException exception = assertThrows(NotValidException.class,
                () -> calculatorService.getScoringResult(getNotValidScoringData()));
        assertEquals(exception.getExceptions().size(), 15);
        assertTrue(exception.getExceptions().contains("Passport issue date must be in past or present"));
    }

    private LoanStatementRequestDto getValidLoanStatementRequestDto() {
        return new LoanStatementRequestDto(getValidScoringData());
    }

    private LoanStatementRequestDto getNotValidLoanStatementRequestDto() {
        var dto = new LoanStatementRequestDto(getNotValidScoringData());
        dto.setEmail("falseEmail.com");
        return dto;
    }

    private ScoringDataDto getValidScoringData() {
        EmploymentDto employment = EmploymentDto.builder()
                .employmentStatus(EmploymentStatus.EMPLOYED)
                .position(Position.MIDDLE_MANAGER)
                .employerINN("123456789012")
                .salary(new BigDecimal("50000"))
                .workExperienceCurrent(12)
                .workExperienceTotal(24)
                .build();

        return ScoringDataDto.builder()
                .firstName("Иван")
                .middleName("Иванович")
                .lastName("Иванов")
                .gender(Gender.MALE)
                .birthdate(LocalDate.now().minusYears(35))
                .maritalStatus(MaritalStatus.MARRIED)
                .employment(employment)
                .accountNumber("40817810099910004312")
                .passportSeries("1234")
                .passportNumber("567890")
                .passportIssueBranch("УФМС г. Москвы")
                .passportIssueDate(LocalDate.now().minusYears(10))
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .dependentAmount(1)
                .amount(new BigDecimal("100000"))
                .term(12)
                .build();
    }

    private ScoringDataDto getNotValidScoringData() {
        EmploymentDto employment = EmploymentDto.builder()
                .employmentStatus(EmploymentStatus.EMPLOYED)
                .position(Position.MIDDLE_MANAGER)
                .employerINN("")
                .salary(new BigDecimal("-50000"))
                .workExperienceCurrent(-12)
                .workExperienceTotal(-24)
                .build();

        return ScoringDataDto.builder()
                .firstName("Ivan")
                .middleName("Ivanovich")
                .lastName("Ivanov")
                .gender(Gender.MALE)
                .birthdate(LocalDate.now().minusYears(5))
                .maritalStatus(MaritalStatus.MARRIED)
                .employment(employment)
                .accountNumber("1")
                .passportSeries("1")
                .passportNumber("1")
                .passportIssueBranch("УФМС г. Москвы")
                .passportIssueDate(LocalDate.now().plusYears(1))
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .dependentAmount(0)
                .amount(new BigDecimal("1000"))
                .term(1)
                .build();
    }
}
