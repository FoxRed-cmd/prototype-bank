package neo.study.deal.service;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import neo.study.deal.entity.Client;
import neo.study.deal.entity.Statement;
import neo.study.deal.repository.StatementRepository;

@Service
@RequiredArgsConstructor
public class StatementService {
    private final StatementRepository statementRepository;

    public Statement save(Statement statement) {
        return statementRepository.save(statement);
    }

    public Statement save(Client client) {
        var statement = Statement.builder().client(client).creationDate(LocalDate.now()).build();
        return statementRepository.save(statement);
    }
}
