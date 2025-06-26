package neo.study.deal.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import neo.study.deal.dto.ApplicationStatus;
import neo.study.deal.dto.ChangeType;
import neo.study.deal.dto.StatementStatusHistoryDto;
import neo.study.deal.entity.Client;
import neo.study.deal.entity.Statement;
import neo.study.deal.repository.StatementRepository;

@Service
@RequiredArgsConstructor
public class StatementService {
    private final StatementRepository statementRepository;

    @Transactional
    public Statement create(Client client) {
        var statement = Statement.builder().client(client).creationDate(LocalDate.now())
                .status(ApplicationStatus.PREAPPROVAL).build();

        addStatusHistory(statement, statement.getStatus(), ChangeType.AUTOMATIC);

        return statementRepository.save(statement);
    }

    @Transactional
    public Statement updateStatus(Statement statement, ApplicationStatus status,
            ChangeType changeType) {
        statement.setStatus(status);

        addStatusHistory(statement, status, changeType);

        return statementRepository.save(statement);
    }

    private void addStatusHistory(Statement statement, ApplicationStatus status,
            ChangeType changeType) {
        var history = new StatementStatusHistoryDto();

        history.setStatus(status);
        history.setTime(OffsetDateTime.now());
        history.setChangeType(changeType);

        statement.getStatusHistory().add(history);
    }

    public Statement getById(UUID id) {
        return statementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Statement not found"));
    }
}
