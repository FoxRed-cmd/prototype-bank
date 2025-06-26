package neo.study.deal.service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
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

@Service
public class DealService {
	private final RestClient restClient;
	private final ClientService clientService;
	private final StatementService statementService;
	private final CreditService creditService;

	@Value("${services.calculator.offers-api}")
	private String offersApi;

	@Value("${services.calculator.calc-api}")
	private String calcApi;

	public DealService(@Value("${services.calculator.base-url}") String baseUrl,
			ClientService clientService, StatementService statementService,
			CreditService creditService) {
		restClient = RestClient.builder().baseUrl(baseUrl)
				.defaultHeader("Content-Type", "application/json")
				.defaultHeader("Accept", "application/json").build();

		this.clientService = clientService;
		this.statementService = statementService;
		this.creditService = creditService;
	}

	public List<LoanOfferDto> statementProcessing(LoanStatementRequestDto request) {
		var offersList = restClient.post().uri(offersApi).body(request).retrieve()
				.body(new ParameterizedTypeReference<List<LoanOfferDto>>() {});

		var client = clientService.create(request);
		var statement = statementService.create(client);

		if (offersList != null && !offersList.isEmpty()) {
			offersList.forEach(offer -> offer.setStatementId(statement.getId()));
			offersList = offersList.stream()
					.sorted(Comparator.comparing(LoanOfferDto::getTerm).reversed()).toList();
		}

		return offersList;
	}

	public void selectOffer(LoanOfferDto offer) {
		var statement = statementService.getById(offer.getStatementId());
		statement.setAppliedOffer(offer);

		statementService.updateStatus(statement, ApplicationStatus.APPROVED, ChangeType.AUTOMATIC);
	}

	public void finishRegistration(String statementId,
			FinishRegistrationRequestDto requestRegistration) {
		var statement = statementService.getById(UUID.fromString(statementId));

		var scoringData = scoringDataSaturation(statement.getClient(), statement.getAppliedOffer(),
				requestRegistration);

		try {
			var creditDto = restClient.post().uri(calcApi).body(scoringData).retrieve()
					.body(CreditDto.class);
			creditService.create(creditDto);

		} catch (HttpClientErrorException ex) {
			statementService.updateStatus(statement, ApplicationStatus.CC_DENIED,
					ChangeType.AUTOMATIC);
			throw ex;
		}

		statementService.updateStatus(statement, ApplicationStatus.CC_APPROVED,
				ChangeType.AUTOMATIC);
	}

	private ScoringDataDto scoringDataSaturation(Client client, LoanOfferDto offer,
			FinishRegistrationRequestDto requestRegistration) {
		var scoringData = new ScoringDataDto();

		scoringData.setAmount(offer.getRequestedAmount());
		scoringData.setTerm(offer.getTerm());

		scoringData.setFirstName(client.getFirstName());
		scoringData.setLastName(client.getLastName());
		scoringData.setMiddleName(client.getMiddleName());
		scoringData.setBirthdate(client.getBirthDate());

		scoringData.setGender(requestRegistration.getGender());

		var passport = client.getPassport();

		scoringData.setPassportSeries(passport.getSeries());
		scoringData.setPassportNumber(passport.getNumber());
		scoringData.setPassportIssueBranch(requestRegistration.getPassportIssueBranch());
		scoringData.setPassportIssueDate(requestRegistration.getPassportIssueDate());

		scoringData.dependentAmount(requestRegistration.getDependentAmount());
		scoringData.maritalStatus(requestRegistration.getMaritalStatus());
		scoringData.setEmployment(requestRegistration.getEmployment());
		scoringData.setAccountNumber(requestRegistration.getAccountNumber());

		scoringData.setIsInsuranceEnabled(offer.getIsInsuranceEnabled());
		scoringData.setIsSalaryClient(offer.getIsSalaryClient());

		return scoringData;
	}
}
