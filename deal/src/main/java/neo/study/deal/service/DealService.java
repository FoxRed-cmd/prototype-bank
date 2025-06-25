package neo.study.deal.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import neo.study.deal.dto.LoanOfferDto;
import neo.study.deal.dto.LoanStatementRequestDto;

@Service
public class DealService {
    private final RestClient restClient;

    @Value("${services.calculator.offers-api}")
    private String offersApi;

    public DealService(@Value("${services.calculator.base-url}") String baseUrl) {
        restClient = RestClient.builder().baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json").build();
    }

    public List<LoanOfferDto> statementProcessing(LoanStatementRequestDto request) {
        return restClient.post().uri(offersApi).body(request).retrieve()
                .body(new ParameterizedTypeReference<List<LoanOfferDto>>() {});
    }
}
