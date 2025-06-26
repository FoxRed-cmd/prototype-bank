package neo.study.deal.service;

import java.util.Comparator;
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
	private final ClientService clientService;
	private final StatementService statementService;

	@Value("${services.calculator.offers-api}")
	private String offersApi;

	public DealService(@Value("${services.calculator.base-url}") String baseUrl,
			ClientService clientService, StatementService statementService) {
		restClient = RestClient.builder().baseUrl(baseUrl)
				.defaultHeader("Content-Type", "application/json")
				.defaultHeader("Accept", "application/json").build();

		this.clientService = clientService;
		this.statementService = statementService;
	}

	public List<LoanOfferDto> statementProcessing(LoanStatementRequestDto request) {
		var offersList = restClient.post().uri(offersApi).body(request).retrieve()
				.body(new ParameterizedTypeReference<List<LoanOfferDto>>() {});

		var client = clientService.save(request);
		var statement = statementService.save(client);

		if (offersList != null && !offersList.isEmpty()) {
			offersList.forEach(offer -> offer.setStatementId(statement.getId()));
			offersList = offersList.stream()
					.sorted(Comparator.comparing(LoanOfferDto::getTerm).reversed()).toList();
		}

		return offersList;
	}
}
