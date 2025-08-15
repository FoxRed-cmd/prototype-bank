package neo.study.deal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import neo.study.deal.dto.ApplicationStatus;
import neo.study.deal.dto.ChangeType;
import neo.study.deal.dto.CreditDto;
import neo.study.deal.dto.LoanStatementRequestDto;
import neo.study.deal.entity.Client;
import neo.study.deal.entity.Credit;
import neo.study.deal.entity.Statement;
import neo.study.deal.repository.ClientRepository;
import neo.study.deal.repository.CreditRepository;
import neo.study.deal.repository.StatementRepository;

@DataJpaTest
@Testcontainers
@Import({ ClientService.class, StatementService.class, CreditService.class })
public class EntityServicesTest {
    @SuppressWarnings("resource")
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CreditService creditService;

    @Autowired
    private CreditRepository creditRepository;

    @Autowired
    private StatementService statementService;

    @Autowired
    private StatementRepository statementRepository;

    @Test
    void testCreateClient_savesCorrectly() {
        var dto = getValidLoanStatementRequestDto();

        Client saved = clientService.create(dto);

        assertNotNull(saved.getId());
        assertEquals("Иван", saved.getFirstName());

        var found = clientRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("ivan@test.com", found.getEmail());
        assertEquals("1234", found.getPassport().getSeries());
    }

    @Test
    void testCreateClient_whenSaveFails_shouldThrowException() {
        var dto = getValidLoanStatementRequestDto();

        ClientRepository mockRepository = Mockito.mock(ClientRepository.class);
        Mockito.when(mockRepository.save(Mockito.any(Client.class)))
                .thenThrow(new DataAccessException("Database error") {
                });

        ClientService serviceWithMock = new ClientService(mockRepository);

        DataAccessException exception = assertThrows(
                DataAccessException.class,
                () -> serviceWithMock.create(dto));

        assertEquals("Database error", exception.getMessage());

        assertTrue(clientRepository.findAll().isEmpty());
    }

    @Test
    void testCreateCredit_savesCorrectly() {
        var dto = getValidCreditDto();

        Credit saved = creditService.create(dto);

        assertNotNull(saved.getId());
        assertEquals(new BigDecimal("100000"), saved.getAmount());

        var found = creditRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(6, found.getTerm());
    }

    @Test
    void testCreateCredit_whenSaveFails_shouldThrowException() {
        var dto = getValidCreditDto();

        CreditRepository mockRepository = Mockito.mock(CreditRepository.class);
        Mockito.when(mockRepository.save(Mockito.any(Credit.class)))
                .thenThrow(new DataAccessException("Database error") {
                });

        CreditService serviceWithMock = new CreditService(mockRepository);

        DataAccessException exception = assertThrows(
                DataAccessException.class,
                () -> serviceWithMock.create(dto));

        assertEquals("Database error", exception.getMessage());

        assertTrue(creditRepository.findAll().isEmpty());
    }

    @Test
    void create_WhenValidClient_ShouldCreateStatement() {
        Client client = new Client();
        client.setFirstName("Test");
        client = clientRepository.save(client);

        Statement result = statementService.create(client);

        assertNotNull(result.getId());
        assertEquals(ApplicationStatus.PREAPPROVAL, result.getStatus());
        assertEquals(client.getId(), result.getClient().getId());
        assertEquals(1, result.getStatusHistory().size());

        Statement dbStatement = statementRepository.findById(result.getId()).orElseThrow();
        assertEquals(result, dbStatement);
    }

    @Test
    void updateStatusById_WhenValidId_ShouldUpdateStatus() {
        // Подготовка данных
        Client client = clientRepository.save(new Client());
        Statement statement = statementService.create(client);

        // Выполнение метода
        Statement updated = statementService.updateStatusById(
                statement.getId(),
                ApplicationStatus.CC_DENIED,
                ChangeType.AUTOMATIC);

        // Проверки
        assertEquals(ApplicationStatus.CC_DENIED, updated.getStatus());
        assertEquals(2, updated.getStatusHistory().size());
    }

    @Test
    void updateStatusById_WhenInvalidId_ShouldThrowEntityNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();

        assertThrows(
                EntityNotFoundException.class,
                () -> statementService.updateStatusById(
                        nonExistentId,
                        ApplicationStatus.APPROVED,
                        ChangeType.MANUAL));
    }

    @Test
    void getById_WhenExists_ShouldReturnStatement() {
        Client client = clientRepository.save(new Client());
        Statement expected = statementService.create(client);

        Statement result = statementService.getById(expected.getId());

        assertEquals(expected, result);
    }

    @Test
    void getById_WhenNotExists_ShouldThrowEntityNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();

        assertThrows(
                EntityNotFoundException.class,
                () -> statementService.getById(nonExistentId));
    }

    private LoanStatementRequestDto getValidLoanStatementRequestDto() {
        var dto = new LoanStatementRequestDto();
        dto.setFirstName("Иван");
        dto.setLastName("Иванов");
        dto.setMiddleName("Иванович");
        dto.setEmail("ivan@test.com");
        dto.setPassportSeries("1234");
        dto.setPassportNumber("567845");
        dto.setBirthDate(LocalDate.of(1990, 1, 1));
        return dto;
    }

    private CreditDto getValidCreditDto() {
        var dto = new CreditDto();
        dto.setAmount(new BigDecimal("100000"));
        dto.setTerm(6);
        dto.setMonthlyPayment(BigDecimal.ONE);
        dto.setRate(BigDecimal.ONE);
        dto.setPsk(BigDecimal.ONE);
        dto.setPaymentSchedule(List.of());
        dto.setIsInsuranceEnabled(true);
        dto.setIsSalaryClient(true);
        return dto;
    }
}
