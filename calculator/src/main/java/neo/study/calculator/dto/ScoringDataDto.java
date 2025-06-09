package neo.study.calculator.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import neo.study.calculator.enums.Gender;
import neo.study.calculator.enums.MaritalStatus;
import neo.study.calculator.utils.validation.AgeConstraint;

@Data
@Builder
public class ScoringDataDto {

    @DecimalMin(value = "20000.0", message = "Loan amount must be at least 20000")
    @NotNull(message = "Amount cannot be null")
    private BigDecimal amount;

    @Min(value = 6, message = "Loan term must be at least 6 months")
    @NotNull(message = "Term cannot be null")
    private Integer term;

    @NotBlank(message = "First name is required")
    @Pattern(regexp = "^[a-zA-Z]{2,30}$", message = "First name must be 2-30 Latin letters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Pattern(regexp = "^[a-zA-Z]{2,30}$", message = "Last name must be 2-30 Latin letters")
    private String lastName;

    @Pattern(regexp = "^[a-zA-Z]{2,30}$|^$",
            message = "Middle name must be 2-30 Latin letters if provided")
    private String middleName;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Birthdate is required")
    @Past(message = "Birthdate must be in the past")
    @AgeConstraint(min = 18, message = "You must be at least 18 years old")
    private LocalDate birthdate;

    @NotBlank(message = "Passport series is required")
    @Pattern(regexp = "^\\d{4}$", message = "Passport series must be 4 digits")
    private String passportSeries;

    @NotBlank(message = "Passport number is required")
    @Pattern(regexp = "^\\d{6}$", message = "Passport number must be 6 digits")
    private String passportNumber;

    @NotNull(message = "Passport issue date is required")
    @PastOrPresent(message = "Passport issue date must be in past or present")
    private LocalDate passportIssueDate;

    @NotBlank(message = "Passport issue branch is required")
    @Size(max = 200, message = "Passport issue branch max length is 200 characters")
    private String passportIssueBranch;

    @NotNull(message = "Marital status is required")
    private MaritalStatus maritalStatus;

    @NotNull(message = "Dependent amount is required")
    @Positive(message = "Dependent amount must be positive")
    private Integer dependentAmount;

    @Valid
    @NotNull(message = "Employment data is required")
    private EmploymentDto employment;

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotNull(message = "Insurance status is required")
    private Boolean isInsuranceEnabled;

    @NotNull(message = "Salary client status is required")
    private Boolean isSalaryClient;
}
