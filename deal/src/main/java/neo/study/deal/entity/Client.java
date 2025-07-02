package neo.study.deal.entity;

import java.time.LocalDate;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import neo.study.deal.dto.Gender;
import neo.study.deal.dto.MaritalStatus;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "client")
@Builder
public class Client {

    @Id
    @GeneratedValue
    @Column(name = "client_id")
    private UUID id;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status")
    private MaritalStatus maritalStatus;

    @Column(name = "dependent_amount")
    private Integer dependentAmount;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "passport")
    private Passport passport;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "employment")
    private Employment employment;

    @Column(name = "account_number")
    private String accountNumber;
}
