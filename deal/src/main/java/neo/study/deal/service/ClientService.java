package neo.study.deal.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import neo.study.deal.dto.LoanStatementRequestDto;
import neo.study.deal.entity.Client;
import neo.study.deal.entity.Passport;
import neo.study.deal.repository.ClientRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    /*
     * Создание клиента и сохранение в базе данных
     */
    public Client create(LoanStatementRequestDto requestDto) {
        log.debug("Start creating client from LoanStatementRequestDto: {}", requestDto);

        var passport = Passport.builder()
                .id(UUID.randomUUID())
                .series(requestDto.getPassportSeries())
                .number(requestDto.getPassportNumber())
                .issueBranch(null)
                .issueDate(null)
                .build();

        log.debug("Created passport: {}", passport);

        var client = Client.builder()
                .firstName(requestDto.getFirstName())
                .lastName(requestDto.getLastName())
                .middleName(requestDto.getMiddleName())
                .email(requestDto.getEmail())
                .birthDate(requestDto.getBirthDate())
                .passport(passport)
                .gender(null)
                .maritalStatus(null)
                .dependentAmount(null)
                .employment(null)
                .accountNumber(null)
                .build();

        log.debug("Created client and saved in DB: {}", client);

        return clientRepository.save(client);
    }
}
