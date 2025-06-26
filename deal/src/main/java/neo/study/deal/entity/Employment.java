package neo.study.deal.entity;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import neo.study.deal.dto.EmploymentStatus;
import neo.study.deal.dto.Position;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employment {
    private EmploymentStatus status;
    private String employerINN;
    private BigDecimal salary;
    private Position position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;
}
