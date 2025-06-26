package neo.study.deal.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import neo.study.deal.dto.ApplicationStatus;
import neo.study.deal.dto.LoanOfferDto;
import neo.study.deal.dto.StatementStatusHistoryDto;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "statement")
@Builder
public class Statement {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "credit_id")
    private Credit credit;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "applied_offer")
    private LoanOfferDto appliedOffer;

    @Column(name = "sign_date")
    private LocalDate signDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "status_history")
    private List<StatementStatusHistoryDto> statusHistory;
}
