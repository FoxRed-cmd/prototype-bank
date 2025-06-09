package neo.study.calculator.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import neo.study.calculator.enums.EmploymentStatus;
import neo.study.calculator.enums.Position;

@Data
@Builder
public class EmploymentDto {

    @NotNull(message = "Employment status is required")
    private EmploymentStatus employmentStatus;

    @NotBlank(message = "Employer INN is required")
    private String employerINN;

    @NotNull(message = "Salary is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be positive")
    private BigDecimal salary;

    @NotNull(message = "Position is required")
    private Position position;

    @NotNull(message = "Total work experience is required")
    @Min(value = 0, message = "Experience cannot be negative")
    private Integer workExperienceTotal;

    @NotNull(message = "Current work experience is required")
    @Min(value = 0, message = "Experience cannot be negative")
    private Integer workExperienceCurrent;
}
