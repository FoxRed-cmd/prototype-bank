package neo.study.calculator.utils.validation;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import neo.study.calculator.dto.EmploymentDto;
import neo.study.calculator.dto.LoanStatementRequestDto;
import neo.study.calculator.dto.ScoringDataDto;
import neo.study.calculator.enums.EmploymentStatus;
import neo.study.calculator.enums.Gender;
import neo.study.calculator.enums.MaritalStatus;
import neo.study.calculator.enums.Position;

public class DtoValidatorTest {
    @Test
    void testEmploymentValidateWhenDtoIsEmpty() {
        EmploymentDto dto = new EmploymentDto(); // все поля null

        List<String> errors = DtoValidator.employmentValidate(dto);

        assertTrue(errors.size() == 6, "Expected 6 errors, got " + errors.size());
    }

    @Test
    void testLoanStatementRequestValidateForInvalidLoanRequest() {
        LoanStatementRequestDto dto = new LoanStatementRequestDto();
        dto.setAmount(new BigDecimal("10000")); // меньше 20000
        dto.setTerm(5); // меньше 6
        dto.setFirstName("A"); // слишком короткое имя
        dto.setLastName(""); // пустое
        dto.setEmail("bademail"); // неправильный формат
        dto.setBirthDate(LocalDate.now()); // сегодня
        dto.setPassportSeries("123"); // слишком коротко
        dto.setPassportNumber("1234567"); // слишком длинно

        List<String> errors = DtoValidator.loanStatementRequestValidate(dto);

        assertTrue(errors.size() == 9, "Expected 9 errors, got " + errors.size());
    }

    @Test
    void testScoringDataValidateWhenDtoIsValid() {

        ScoringDataDto dto = new ScoringDataDto();
        dto.setAmount(new BigDecimal("50000"));
        dto.setTerm(12);
        dto.setFirstName("Джон");
        dto.setLastName("Доу");
        dto.setMiddleName("Алекс");
        dto.setGender(Gender.MALE);
        dto.setBirthdate(LocalDate.of(1990, 1, 1));
        dto.setPassportSeries("1234");
        dto.setPassportNumber("567890");
        dto.setPassportIssueDate(LocalDate.of(2010, 1, 1));
        dto.setPassportIssueBranch("MVD 770-001");
        dto.setMaritalStatus(MaritalStatus.MARRIED);
        dto.setDependentAmount(1);
        dto.setEmployment(new EmploymentDto());
        dto.getEmployment().setEmploymentStatus(EmploymentStatus.EMPLOYED);
        dto.getEmployment().setEmployerINN("1234567890");
        dto.getEmployment().setSalary(new BigDecimal("30000"));
        dto.getEmployment().setPosition(Position.MIDDLE_MANAGER);
        dto.getEmployment().setWorkExperienceTotal(10);
        dto.getEmployment().setWorkExperienceCurrent(2);
        dto.setAccountNumber("12345678901234567890");
        dto.setIsInsuranceEnabled(true);
        dto.setIsSalaryClient(false);

        List<String> errors = DtoValidator.scoringDataValidate(dto);

        assertTrue(errors.isEmpty(), "Expected 0 errors, got " + errors.size());
    }
}
