package neo.study.deal.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import neo.study.deal.dto.ApplicationStatus;
import neo.study.deal.dto.ChangeType;
import neo.study.deal.dto.StatementStatusHistoryDto;
import neo.study.deal.entity.Client;
import neo.study.deal.entity.Statement;
import neo.study.deal.repository.StatementRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatementService {
    private final StatementRepository statementRepository;

    /*
     * Создание заявки со ссылкой на клиента в базе данных
     */
    public Statement create(Client client) {
        log.debug("Start creating statement for client: {}", client);

        var statement = Statement.builder()
                .client(client)
                .credit(null)
                .creationDate(LocalDate.now())
                .status(ApplicationStatus.PREAPPROVAL)
                .appliedOffer(null)
                .signDate(null)
                .build();

        addStatusHistory(statement, statement.getStatus(), ChangeType.AUTOMATIC);

        log.debug("Created statement and saved in DB: {}", statement);

        return statementRepository.save(statement);
    }

    public Statement update(Statement statement) {
        log.debug("Start updating statement: {}", statement);
        return statementRepository.save(statement);
    }

    /*
     * Обновление статуса заявки в базе данных
     */
    public Statement updateStatus(Statement statement, ApplicationStatus status,
            ChangeType changeType) {
        log.debug("Updating status for statement: {}", statement);
        statement.setStatus(status);

        log.debug("New status: {}, change type: {}", status, changeType);

        addStatusHistory(statement, status, changeType);

        log.debug("Updated statement and saved in DB: {}", statement);

        return statementRepository.save(statement);
    }

    public Statement updateStatusById(UUID id, ApplicationStatus status, ChangeType changeType) {
        var statement = getById(id);
        return updateStatus(statement, status, changeType);
    }

    /*
     * Метод для добавления статуса в историю статусов заявки
     */
    private void addStatusHistory(Statement statement, ApplicationStatus status,
            ChangeType changeType) {
        var history = new StatementStatusHistoryDto();

        history.setStatus(status);
        history.setTime(OffsetDateTime.now());
        history.setChangeType(changeType);

        statement.getStatusHistory().add(history);

        log.debug("Added status history: {}", history);
    }

    /*
     * Метод для получения заявки из базы данных по id
     */
    public Statement getById(UUID id) {
        return statementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Statement not found"));
    }
}
