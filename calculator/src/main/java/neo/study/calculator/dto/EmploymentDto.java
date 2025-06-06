package neo.study.calculator.dto;

import java.math.BigDecimal;
import javax.swing.text.Position;
import lombok.Data;
import neo.study.calculator.enums.EmploymentStatus;

@Data
public class EmploymentDto {
    private EmploymentStatus employmentStatus;
    private String employerINN;
    private BigDecimal salary;
    private Position position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;
}
