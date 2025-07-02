package neo.study.deal.entity;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Passport {
    private UUID id;
    private String series;
    private String number;
    private String issueBranch;
    private LocalDate issueDate;
}
