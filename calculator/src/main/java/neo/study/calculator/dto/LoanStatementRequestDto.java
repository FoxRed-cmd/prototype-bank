package neo.study.calculator.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import neo.study.calculator.utils.validation.AgeConstraint;

@Data
public class LoanStatementRequestDto {

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

    @NotBlank(message = "Email is required")
    @Email(regexp = "^[a-z0-9A-Z_!#$%&'*+/=?`{|}~^.-]+@[a-z0-9A-Z.-]+$",
            message = "Invalid email format")
    private String email;

    @NotNull(message = "Birthdate is required")
    @Past(message = "Birthdate must be in the past")
    @AgeConstraint(min = 18, message = "You must be at least 18 years old")
    private LocalDate birthDate;

    @NotBlank(message = "Passport series is required")
    @Pattern(regexp = "^\\d{4}$", message = "Passport series must be 4 digits")
    private String passportSeries;

    @NotBlank(message = "Passport number is required")
    @Pattern(regexp = "^\\d{6}$", message = "Passport number must be 6 digits")
    private String passportNumber;
}
