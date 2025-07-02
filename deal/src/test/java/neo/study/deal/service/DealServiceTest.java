package neo.study.deal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.eq;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.util.ReflectionTestUtils;
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
        private RestClient.RequestBodyUriSpec requestSpec;
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

                var passport = new Passport(UUID.randomUUID(), "1234", "567890", null, null);
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
        void finishRegistration_calculationFails_updatesToDenied() {
                String statementId = UUID.randomUUID().toString();
                FinishRegistrationRequestDto request = new FinishRegistrationRequestDto();
                Statement statement = mock(Statement.class);
                Client client = new Client();
                LoanOfferDto offer = new LoanOfferDto();
                ScoringDataDto scoringData = new ScoringDataDto();

                when(statementService.getById(UUID.fromString(statementId))).thenReturn(statement);
                when(statement.getClient()).thenReturn(client);
                when(statement.getAppliedOffer()).thenReturn(offer);

                DealService spyService = Mockito.spy(dealService);
                doReturn(scoringData).when(spyService).fillScoringData(client, offer, request);

                when(restClient.post()).thenReturn(requestSpec);
                when(requestSpec.uri("/calculator/calc")).thenReturn(bodySpec);
                when(bodySpec.body(scoringData)).thenReturn(bodySpec);
                when(bodySpec.retrieve()).thenReturn(responseSpec);
                when(responseSpec.body(CreditDto.class)).thenThrow(new RuntimeException("API failed"));

                RuntimeException thrown = assertThrows(RuntimeException.class,
                                () -> spyService.finishRegistration(statementId, request));

                assertEquals("API failed", thrown.getMessage());
                verify(creditService, never()).create(any());
                verify(statementService, never()).updateStatus(any(), eq(ApplicationStatus.CC_APPROVED), any());
        }
}
