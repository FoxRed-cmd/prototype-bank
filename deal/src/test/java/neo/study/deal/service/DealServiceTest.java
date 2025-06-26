package neo.study.deal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import neo.study.deal.dto.ApplicationStatus;
import neo.study.deal.dto.ChangeType;
import neo.study.deal.dto.CreditDto;
import neo.study.deal.dto.FinishRegistrationRequestDto;
import neo.study.deal.dto.LoanOfferDto;
import neo.study.deal.dto.LoanStatementRequestDto;
import neo.study.deal.dto.ScoringDataDto;
import neo.study.deal.entity.Client;
import neo.study.deal.entity.Credit;
import neo.study.deal.entity.Passport;
import neo.study.deal.entity.Statement;

@ExtendWith(MockitoExtension.class)
public class DealServiceTest {
    @Mock
    private RestClient restClient;
    @Mock
    private RestClient.RequestBodyUriSpec uriSpec;
    @Mock
    private RestClient.RequestBodySpec bodySpec;
    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Mock
    private ClientService clientService;
    @Mock
    private StatementService statementService;
    @Mock
    private CreditService creditService;

    @InjectMocks
    private DealService dealService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(dealService, "offersApi", "/calculator/offers");
        ReflectionTestUtils.setField(dealService, "calcApi", "/calculator/calc");
    }

    @SuppressWarnings("unchecked")
    @Test
    void testStatementProcessing() {
        var request = new LoanStatementRequestDto();
        var offer = new LoanOfferDto();
        offer.setTerm(12);
        var offerList = List.of(offer);

        var client = new Client();
        var statement = new Statement();
        statement.setId(UUID.randomUUID());

        // Mocking restClient chain
        Mockito.when(restClient.post()).thenReturn(uriSpec);
        Mockito.when(uriSpec.uri("/calculator/offers")).thenReturn(bodySpec);
        Mockito.when(bodySpec.body(request)).thenReturn(bodySpec);
        Mockito.when(bodySpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.body(Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(offerList);

        Mockito.when(clientService.create(request)).thenReturn(client);
        Mockito.when(statementService.create(client)).thenReturn(statement);

        var result = dealService.statementProcessing(request);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(statement.getId(), result.get(0).getStatementId());
    }

    @Test
    void testSelectOffer() {
        var offer = new LoanOfferDto();
        var statementId = UUID.randomUUID();
        offer.setStatementId(statementId);

        var statement = new Statement();
        Mockito.when(statementService.getById(statementId)).thenReturn(statement);
        Mockito.when(statementService.updateStatus(statement, ApplicationStatus.APPROVED,
                ChangeType.AUTOMATIC)).thenReturn(statement);

        dealService.selectOffer(offer);

        Mockito.verify(statementService).getById(statementId);
        Mockito.verify(statementService).updateStatus(statement, ApplicationStatus.APPROVED,
                ChangeType.AUTOMATIC);
    }

    @Test
    void testFinishRegistration_Success() {
        var statementId = UUID.randomUUID();
        var request = new FinishRegistrationRequestDto();
        var appliedOffer = new LoanOfferDto();
        appliedOffer.setRequestedAmount(BigDecimal.TEN);
        appliedOffer.setTerm(6);

        var client = new Client();
        client.setBirthDate(LocalDate.of(1990, 1, 1));
        client.setFirstName("Ivan");
        client.setLastName("Ivanov");
        client.setMiddleName("Ivanovich");

        var passport = new Passport("1234", "567890", null, null);
        client.setPassport(passport);

        var statement = new Statement();
        statement.setClient(client);
        statement.setAppliedOffer(appliedOffer);

        var creditDto = new CreditDto();

        Mockito.when(statementService.getById(statementId)).thenReturn(statement);
        Mockito.when(restClient.post()).thenReturn(uriSpec);
        Mockito.when(uriSpec.uri("/calculator/calc")).thenReturn(bodySpec);
        Mockito.when(bodySpec.body(Mockito.any(ScoringDataDto.class))).thenReturn(bodySpec);
        Mockito.when(bodySpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.body(CreditDto.class)).thenReturn(creditDto);

        Mockito.when(creditService.create(creditDto)).thenReturn(new Credit());
        Mockito.when(statementService.updateStatus(statement, ApplicationStatus.CC_APPROVED,
                ChangeType.AUTOMATIC)).thenReturn(statement);

        dealService.finishRegistration(statementId.toString(), request);

        Mockito.verify(creditService).create(creditDto);
    }

    @Test
    void testFinishRegistration_HttpError() {
        var statementId = UUID.randomUUID();
        var statement = new Statement();
        var client = new Client();
        client.setFirstName("Ivan");
        client.setLastName("Ivanov");
        client.setMiddleName("Ivanovich");
        client.setBirthDate(LocalDate.of(1990, 1, 1));

        var passport = new Passport();
        passport.setSeries("1234");
        passport.setNumber("567890");
        client.setPassport(passport);
        statement.setClient(client);
        statement.setAppliedOffer(new LoanOfferDto());

        Mockito.when(statementService.getById(statementId)).thenReturn(statement);
        Mockito.when(restClient.post()).thenReturn(uriSpec);
        Mockito.when(uriSpec.uri("/calculator/calc")).thenReturn(bodySpec);
        Mockito.when(bodySpec.body(Mockito.any(ScoringDataDto.class))).thenReturn(bodySpec);
        Mockito.when(bodySpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.body(CreditDto.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThrows(HttpClientErrorException.class, () -> {
            dealService.finishRegistration(statementId.toString(),
                    new FinishRegistrationRequestDto());
        });

        Mockito.verify(statementService).updateStatus(statement, ApplicationStatus.CC_DENIED,
                ChangeType.AUTOMATIC);
    }
}
