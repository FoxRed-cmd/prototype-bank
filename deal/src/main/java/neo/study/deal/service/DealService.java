package neo.study.deal.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import lombok.extern.slf4j.Slf4j;
import neo.study.deal.dto.ApplicationStatus;
import neo.study.deal.dto.ChangeType;
import neo.study.deal.dto.CreditDto;
import neo.study.deal.dto.FinishRegistrationRequestDto;
import neo.study.deal.dto.LoanOfferDto;
import neo.study.deal.dto.LoanStatementRequestDto;
import neo.study.deal.dto.ScoringDataDto;
import neo.study.deal.entity.Client;
import neo.study.deal.entity.Statement;

@Slf4j
@Service
public class DealService {
	private final RestClient restClient;
	private final ClientService clientService;
	private final StatementService statementService;
	private final CreditService creditService;
	private final KafkaTemplate<String, String> kafkaTemplate;

	@Value("${services.calculator.offers-api}")
	private String offersApi;

	@Value("${services.calculator.calc-api}")
	private String calcApi;

	public DealService(RestClient restClient, ClientService clientService,
			StatementService statementService, CreditService creditService,
			KafkaTemplate<String, String> kafkaTemplate) {
		this.restClient = restClient;
		this.clientService = clientService;
		this.statementService = statementService;
		this.creditService = creditService;
		this.kafkaTemplate = kafkaTemplate;
	}

	/*
	 * Расчёт возможных условий кредита
	 *
	 * По API приходит LoanStatementRequestDto
	 *
	 * На основе LoanStatementRequestDto создаётся сущность Client и сохраняется в
	 * БД.
	 *
	 * Создаётся Statement со связью на только что созданный Client и сохраняется в
	 * БД.
	 *
	 * Отправляется POST запрос на /calculator/offers МС Калькулятор через
	 * RestClient
	 *
	 * Каждому элементу из списка List<LoanOfferDto> присваивается id созданной
	 * заявки (Statement)
	 *
	 * Ответ на API - список из 4х LoanOfferDto от "худшего" к "лучшему".
	 */
	@Transactional
	public List<LoanOfferDto> statementProcessing(LoanStatementRequestDto request) {
		log.info("Start processing statement with input data: {}", request);

		var client = clientService.create(request);
		var statement = statementService.create(client);

		log.debug("Client created in DB: {}", client);
		log.debug("Statement created in DB: {}", statement);

		return Optional.ofNullable(getLoanOffers(request))
				.map(offers -> enrichAndSortOffers(offers, statement.getId()))
				.orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST,
						"The service returned an empty list of offers"));
	}

	/*
	 * Получает предложения кредита по API
	 */
	private List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto request) {
		return restClient.post().uri(offersApi).body(request).retrieve()
				.body(new ParameterizedTypeReference<>() {
				});
	}

	/*
	 * Дополняет предложения кредита и сортирует по уменьшению процентной ставки
	 */
	private List<LoanOfferDto> enrichAndSortOffers(List<LoanOfferDto> offers, UUID statementId) {
		log.info("Offers list result:");
		var sortedOffers = offers.stream().peek(offer -> offer.setStatementId(statementId))
				.sorted(Comparator.comparing(LoanOfferDto::getRate).reversed()).toList();
		sortedOffers.forEach(offer -> log.info("{}", offer));
		return sortedOffers;
	}

	/*
	 * Выбор одного из предложений
	 *
	 * По API приходит LoanOfferDto
	 *
	 * Достаётся из БД заявка(Statement) по statementId из LoanOfferDto.
	 *
	 * В заявке обновляется статус, история
	 * статусов(List<StatementStatusHistoryDto>), принятое
	 * предложение LoanOfferDto устанавливается в поле appliedOffer.
	 *
	 * Заявка сохраняется.
	 */
	@Transactional
	public void selectOffer(LoanOfferDto offer) {
		log.info("Selected offer: {}", offer);
		var statement = statementService.getById(offer.getStatementId());
		statement.setAppliedOffer(offer);

		statement = statementService.updateStatus(statement, ApplicationStatus.APPROVED,
				ChangeType.AUTOMATIC);

		var clientEmail = statement.getClient().getEmail();

		kafkaTemplate.send("finish-registration", clientEmail);

		log.debug("Statement updated in DB: {}", statement);
	}

	/*
	 * Завершение регистрации + полный подсчёт кредита
	 *
	 * По API приходит объект FinishRegistrationRequestDto и параметр statementId
	 * (String).
	 *
	 * Достаётся из БД заявка(Statement) по statementId.
	 *
	 * ScoringDataDto насыщается информацией из FinishRegistrationRequestDto и
	 * Client, который
	 * хранится в Statement
	 *
	 * Отправляется POST запрос на /calculator/calc МС Калькулятор с телом
	 * ScoringDataDto через
	 * RestClient.
	 *
	 * На основе полученного из кредитного конвейера CreditDto создаётся сущность
	 * Credit и
	 * сохраняется в базу со статусом CALCULATED.
	 *
	 * В заявке обновляется статус, история статусов.
	 *
	 * Заявка сохраняется.
	 */

	public void finishRegistration(String statementId,
			FinishRegistrationRequestDto requestRegistration) {
		log.info("Finish registration for statement: {}", statementId);
		log.info("Input data: {}", requestRegistration);

		var statement = statementService.getById(UUID.fromString(statementId));
		var scoringData = fillScoringData(statement.getClient(), statement.getAppliedOffer(),
				requestRegistration);

		log.debug("Statement: {}", statement);
		log.debug("Scoring data: {}", scoringData);

		var creditDto = calculateCredit(scoringData);
		processRegistration(creditDto, statement);
	}

	/*
	 * Получает условия кредита по API
	 */
	private CreditDto calculateCredit(ScoringDataDto scoringData) {
		return restClient.post().uri(calcApi).body(scoringData).retrieve().body(CreditDto.class);
	}

	/*
	 * Обработка результата кредитного конвейера
	 */
	@Transactional
	private void processRegistration(CreditDto creditDto, Statement statement) {
		log.info("Start process registration for credit: {}", creditDto);

		var credit = creditService.create(creditDto);

		log.debug("Created credit in DB: {}", credit);

		statement = statementService.updateStatus(statement, ApplicationStatus.CC_APPROVED,
				ChangeType.AUTOMATIC);

		log.debug("Updated statement in DB: {}", statement);
	}

	/*
	 * Метод насыщения ScoringDataDto данными из FinishRegistrationRequestDto,
	 * Client, AppliedOffer
	 */
	ScoringDataDto fillScoringData(Client client, LoanOfferDto appliedOffer,
			FinishRegistrationRequestDto requestRegistration) {
		log.debug("Start fill scoring data");
		log.debug("Client: {}", client);
		log.debug("Applied offer: {}", appliedOffer);
		log.debug("Request registration: {}", requestRegistration);

		var scoringData = new ScoringDataDto();

		scoringData.setAmount(appliedOffer.getRequestedAmount());
		scoringData.setTerm(appliedOffer.getTerm());

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

		scoringData.setIsInsuranceEnabled(appliedOffer.getIsInsuranceEnabled());
		scoringData.setIsSalaryClient(appliedOffer.getIsSalaryClient());

		log.debug("Filled scoring data: {}", scoringData);
		return scoringData;
	}
}
