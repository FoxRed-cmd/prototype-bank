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
import neo.study.gateway.dto.ApplicationStatus;
import neo.study.gateway.dto.FinishRegistrationRequestDto;
import neo.study.gateway.dto.LoanOfferDto;
import neo.study.gateway.dto.LoanStatementRequestDto;
import neo.study.gateway.dto.StatementDto;

@Service
@RequiredArgsConstructor
public class GatewayService {

    @Value("${services.statement.process-statement-api}")
    private String processStatementApi;

    @Value("${services.statement.select-offer-api}")
    private String selectOfferApi;

    @Value("${services.deal.complete-registration-api}")
    private String completeRegistrationApi;

    @Value("${services.deal.send-documents-api}")
    private String sendDocumentsApi;

    @Value("${services.deal.sign-documents-api}")
    private String signDocumentsApi;

    @Value("${services.deal.code-documents-api}")
    private String codeDocumentsApi;

    @Value("${services.deal.get-statement-api}")
    private String getStatementApi;

    @Value("${services.deal.update-statement-api}")
    private String updateStatementApi;

    private final RestClient restClientToStatement;
    private final RestClient restClientToDeal;

    public List<LoanOfferDto> processStatement(LoanStatementRequestDto requestDto) {
        var offers = Optional.ofNullable(getLoanOffers(requestDto))
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                        "The service returned an empty list of offers"));
        return offers;
    }

    public void selectOffer(LoanOfferDto selectedOffer) {
        restClientToStatement.post()
                .uri(selectOfferApi)
                .body(selectedOffer)
                .retrieve()
                .toBodilessEntity();
    }

    public void completeRegistration(String statementId, FinishRegistrationRequestDto requestRegistration) {
        restClientToDeal.post()
                .uri(completeRegistrationApi.replace("{statementId}", statementId))
                .body(requestRegistration)
                .retrieve()
                .toBodilessEntity();
    }

    public void sendDocuments(String statementId) {
        restClientToDeal.post()
                .uri(sendDocumentsApi.replace("{statementId}", statementId))
                .retrieve()
                .toBodilessEntity();
    }

    public void signDocuments(String statementId) {
        restClientToDeal.post()
                .uri(signDocumentsApi.replace("{statementId}", statementId))
                .retrieve()
                .toBodilessEntity();
    }

    public void codeDocuments(String statementId) {
        restClientToDeal.post()
                .uri(codeDocumentsApi.replace("{statementId}", statementId))
                .retrieve()
                .toBodilessEntity();
    }

    public StatementDto getStatement(String statementId) {
        return restClientToDeal.get()
                .uri(getStatementApi.replace("{statementId}", statementId))
                .retrieve()
                .body(StatementDto.class);
    }

    public StatementDto updateStatementStatus(String statementId, ApplicationStatus status) {
        return restClientToDeal.put()
                .uri(updateStatementApi.replace("{statementId}", statementId))
                .body(status)
                .retrieve()
                .body(StatementDto.class);
    }

    private List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto requestDto) {
        return restClientToStatement.post()
                .uri(processStatementApi)
                .body(requestDto)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}
