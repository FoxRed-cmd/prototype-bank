package neo.study.deal.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import neo.study.deal.dto.LoanStatementRequestDto;
import neo.study.deal.entity.Client;
import neo.study.deal.entity.Passport;
import neo.study.deal.repository.ClientRepository;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    public Client save(LoanStatementRequestDto requestDto) {
        var passport = Passport.builder().series(requestDto.getPassportSeries())
                .number(requestDto.getPassportNumber()).build();

        var client = Client.builder().firstName(requestDto.getFirstName())
                .lastName(requestDto.getLastName()).middleName(requestDto.getMiddleName())
                .email(requestDto.getEmail()).birthDate(requestDto.getBirthDate())
                .passport(passport).build();

        return clientRepository.save(client);
    }
}
