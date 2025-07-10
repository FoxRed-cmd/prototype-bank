# prototype-bank

## 🔧 Technologies

-   **Java:** 21 (Oracle JDK)
-   **Maven:** 3.9.9
-   **Maven Wrapper:** 3.3.2
-   **Spring Boot:** 3.x
-   **Lombok:** для генерации шаблонного кода
-   **Openapi generator maven plugin:** генерация моделей из спецификации openapi.yaml
-   **Liquibase:** миграции

## SwaggerUI

-   **MVP 1 Calculator:** http://localhost:8080/swagger-ui/index.html
-   **MVP 2 Deal:** http://localhost:8081/swagger-ui/index.html
-   **MVP 3 Statement:** http://localhost:8082/swagger-ui/index.html

## Valid JSON examples

### LoanStatementRequestDto

```JSON
{
  "amount": 150000,
  "term": 12,
  "firstName": "Иван",
  "lastName": "Иванов",
  "middleName": "Иванович",
  "email": "3J7wI@example.com",
  "birthDate": "2000-01-01",
  "passportSeries": "1234",
  "passportNumber": "123456"
}
```

### ScoringDataDto

```JSON
{
  "amount": 150000,
  "term": 12,
  "firstName": "Иван",
  "lastName": "Иванов",
  "middleName": "Иванович",
  "gender": "MALE",
  "birthdate": "2000-01-01",
  "passportSeries": "1234",
  "passportNumber": "123456",
  "passportIssueDate": "2020-01-01",
  "passportIssueBranch": "Отделение УФМС города Москвы",
  "maritalStatus": "SINGLE",
  "dependentAmount": 1,
  "employment": {
    "employmentStatus": "EMPLOYED",
    "employerINN": "123456789021",
    "salary": 43000,
    "position": "MIDDLE_MANAGER",
    "workExperienceTotal": 24,
    "workExperienceCurrent": 12
  },
  "accountNumber": "12345678901234567000",
  "isInsuranceEnabled": false,
  "isSalaryClient": false
}
```

### FinishRegistrationRequestDto

```JSON
{
  "gender": "MALE",
  "maritalStatus": "SINGLE",
  "dependentAmount": 1,
  "passportIssueDate": "2010-01-01",
  "passportIssueBranch": "УФМС г. Москвы",
  "employment": {
    "employmentStatus": "EMPLOYED",
    "employerINN": "123456789021",
    "salary": 43000,
    "position": "MIDDLE_MANAGER",
    "workExperienceTotal": 24,
    "workExperienceCurrent": 12
  },
  "accountNumber": "12345678901234567000"
}
```
