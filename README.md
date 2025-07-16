# prototype-bank

## 🔧 Technologies

-   **Java:** 21 (Oracle JDK)
-   **Maven:** 3.9.9
-   **Maven Wrapper:** 3.3.2
-   **Spring Boot:** 3.x
-   **Lombok:** для генерации шаблонного кода
-   **Openapi generator maven plugin:** генерация моделей из спецификации openapi.yaml
-   **Liquibase:** миграции

## 🚀 Startup

### `PowerShell`

### Install java and maven and set environment variables

[Open JDK 21](https://jdk.java.net/21/) or Oracle JDK

[Maven](https://maven.apache.org/)

Environment variables

JAVA_HOME=.../jdk_folder

M2_HOME=.../maven_folder

PATH=%JAVA_HOME%\bin;%M2_HOME%\bin

### Check install

```Shell
java -version

mvn -v
```

### 1. Open repository folder

```Shell
cd prototype-bank
```

### 2. Launch of the calculator service

```Shell
mvn -f .\calculator\pom.xml spring-boot:run
```

### 3. Launch of the deal service

```Shell
${env:URL_DB}='YOUR DB URL'; ${env:USER_DB}='YOUR USERNAME'; ${env:PASSWORD_DB}='YOUR STRONG PASSWORD'; mvn -f .\deal\pom.xml spring-boot:run
```

or with default values

```YAML
spring:
    application:
        name: deal
    datasource:
        url: ${URL_DB:jdbc:postgresql://localhost:5432/TEST}
        username: ${USER_DB:test}
        password: ${PASSWORD_DB:test}
        driver-class-name: org.postgresql.Driver
```

```Shell
mvn -f .\deal\pom.xml spring-boot:run
```

### 4. Launch of the statement service

```Shell
mvn -f .\statement\pom.xml spring-boot:run
```

### Run all services in the background

```Shell
mvn -f .\calculator\pom.xml spring-boot:run & mvn -f .\statement\pom.xml spring-boot:run & ${env:URL_DB}='YOUR DB URL'; ${env:USER_DB}='YOUR USERNAME'; ${env:PASSWORD_DB}='YOUR STRONG PASSWORD'; mvn -f .\deal\pom.xml spring-boot:run
```

## 📗 SwaggerUI

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
