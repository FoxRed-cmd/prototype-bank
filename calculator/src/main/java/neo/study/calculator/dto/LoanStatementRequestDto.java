package neo.study.calculator.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanStatementRequestDto {
    private BigDecimal amount;
    private Integer term;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private LocalDate birthDate;
    private String passportSeries;
    private String passportNumber;

    public LoanStatementRequestDto(ScoringDataDto scoringData) {
        this.amount = scoringData.getAmount();
        this.term = scoringData.getTerm();
        this.firstName = scoringData.getFirstName();
        this.lastName = scoringData.getLastName();
        this.middleName = scoringData.getMiddleName();
        this.email = "trueEmail@mail.com";
        this.birthDate = scoringData.getBirthdate();
        this.passportSeries = scoringData.getPassportSeries();
        this.passportNumber = scoringData.getPassportNumber();
    }
}
