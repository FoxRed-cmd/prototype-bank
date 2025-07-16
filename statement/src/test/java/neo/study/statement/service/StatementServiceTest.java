package neo.study.statement.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import neo.study.statement.dto.LoanOfferDto;
import neo.study.statement.dto.LoanStatementRequestDto;

@ExtendWith(MockitoExtension.class)
public class StatementServiceTest {

    @Mock
    private RestClient restClient;
    @Mock
    private RestClient.RequestBodyUriSpec uriSpec;
    @Mock
    private RestClient.RequestBodySpec bodySpec;
    @Mock
    private RestClient.ResponseSpec responseSpec;
    @InjectMocks
    private StatementService statementService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(statementService, "statementApi", "/deal/statement");
        ReflectionTestUtils.setField(statementService, "selectApi", "/deal/offer/select");
    }

    @Test
    void testStatementProcessing_success() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();
        List<LoanOfferDto> offers = List.of(new LoanOfferDto(), new LoanOfferDto(), new LoanOfferDto(),
                new LoanOfferDto());

        Mockito.when(restClient.post()).thenReturn(uriSpec);
        Mockito.when(uriSpec.uri("/deal/statement")).thenReturn(bodySpec);
        Mockito.when(bodySpec.body(request)).thenReturn(bodySpec);
        Mockito.when(bodySpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.body(Mockito.any(ParameterizedTypeReference.class))).thenReturn(offers);

        List<LoanOfferDto> result = statementService.statementProcessing(request);

        assertEquals(4, result.size());
    }

    @Test
    void testStatementProcessing_emptyOffers_shouldThrow() {
        LoanStatementRequestDto request = new LoanStatementRequestDto();

        Mockito.when(restClient.post()).thenReturn(uriSpec);
        Mockito.when(uriSpec.uri("/deal/statement")).thenReturn(bodySpec);
        Mockito.when(bodySpec.body(request)).thenReturn(bodySpec);
        Mockito.when(bodySpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.body(Mockito.any(ParameterizedTypeReference.class))).thenReturn(null);

        HttpClientErrorException ex = assertThrows(
                HttpClientErrorException.class,
                () -> statementService.statementProcessing(request));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testSelectOffer_success() {
        LoanOfferDto offer = new LoanOfferDto();

        Mockito.when(restClient.post()).thenReturn(uriSpec);
        Mockito.when(uriSpec.uri("/deal/offer/select")).thenReturn(bodySpec);
        Mockito.when(bodySpec.body(offer)).thenReturn(bodySpec);
        Mockito.when(bodySpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.toBodilessEntity()).thenReturn(ResponseEntity.ok().build());

        assertDoesNotThrow(() -> statementService.selectOffer(offer));
    }
}
