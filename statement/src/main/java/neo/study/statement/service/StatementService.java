package neo.study.statement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;
import neo.study.statement.dto.LoanOfferDto;
import neo.study.statement.dto.LoanStatementRequestDto;

@Slf4j
@Service
public class StatementService {
    @Value("${services.deal.statement-api}")
    private String statementApi;

    @Value("${services.deal.select-api}")
    private String selectApi;

    private final RestClient restClient;

    public StatementService(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<LoanOfferDto> processStatement(LoanStatementRequestDto request) {
        log.info("Starting statement processing for request: {}", request);
        List<LoanOfferDto> offers = Optional.ofNullable(getLoanOffers(request))
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                        "The service returned an empty list of offers"));

        log.info("Received offers:");
        offers.forEach(offer -> log.info("Offer result: {}", offer));

        return offers;
    }

    public void selectOffer(LoanOfferDto selectedOffer) {
        log.info("Selecting offer: {}", selectedOffer);
        restClient.post().uri(selectApi).body(selectedOffer).retrieve().toBodilessEntity();
    }

    private List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto request) {
        return restClient.post().uri(statementApi).body(request).retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

    }
}
