package neo.study.calculator.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import neo.study.calculator.enums.EmploymentStatus;
import neo.study.calculator.enums.Position;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmploymentDto {
    private EmploymentStatus employmentStatus;
    private String employerINN;
    private BigDecimal salary;
    private Position position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;
}
