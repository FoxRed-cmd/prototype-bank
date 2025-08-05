package neo.study.gateway.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import neo.study.deal.dto.LoanOfferDto;
import neo.study.deal.dto.LoanStatementRequestDto;

@Service
@RequiredArgsConstructor
public class GatewayService {

    @Value("${services.statement.process-statement-api}")
    private String processStatementApi;

    @Value("${services.statement.select-offer-api}")
    private String selectOfferApi;

    private final RestClient restClientToStatement;

    public List<LoanOfferDto> processStatement(LoanStatementRequestDto requestDto) {
        var offers = Optional.ofNullable(getLoanOffers(requestDto))
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                        "The service returned an empty list of offers"));
        return offers;
    }

    public void selectOffer(LoanOfferDto selectedOffer) {
        restClientToStatement.post().uri(selectOfferApi).body(selectedOffer).retrieve().toBodilessEntity();
    }

    private List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto requestDto) {
        return restClientToStatement.post().uri(processStatementApi).body(requestDto).retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}
