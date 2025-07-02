package neo.study.deal.entity;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import neo.study.deal.dto.EmploymentStatus;
import neo.study.deal.dto.Position;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Employment {
    private UUID id;
    private EmploymentStatus status;
    private String employerINN;
    private BigDecimal salary;
    private Position position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;
}
