package neo.study.calculator.utils.validation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import neo.study.calculator.dto.EmploymentDto;
import neo.study.calculator.dto.LoanStatementRequestDto;
import neo.study.calculator.dto.ScoringDataDto;

public class DtoValidator {
    /*
     * Все поля не должны быть null или пустыми
     *
     * Зарплата должна быть положительным значением
     *
     * Стаж должен быть положительным значением
     *
     */
    public static List<String> employmentValidate(EmploymentDto dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getEmploymentStatus() == null) {
            errors.add("Employment status is required");
        }

        if (dto.getEmployerINN() == null || dto.getEmployerINN().trim().isEmpty()) {
            errors.add("Employer INN is required");
        }

        if (dto.getSalary() == null) {
            errors.add("Salary is required");
        } else if (dto.getSalary().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Salary must be positive");
        }

        if (dto.getPosition() == null) {
            errors.add("Position is required");
        }

        if (dto.getWorkExperienceTotal() == null) {
            errors.add("Total work experience is required");
        } else if (dto.getWorkExperienceTotal() < 0) {
            errors.add("Experience cannot be negative (total)");
        }

        if (dto.getWorkExperienceCurrent() == null) {
            errors.add("Current work experience is required");
        } else if (dto.getWorkExperienceCurrent() < 0) {
            errors.add("Experience cannot be negative (current)");
        }

        return errors;
    }

    /*
     * Все поля не должны быть null или пустыми
     *
     * Сумма кредита - действительно число, большее или равное 20000 и меньшее или равное 5000000
     *
     * Имя, Фамилия - от 2 до 30 букв (кириллица). Отчество, при наличии - от 2 до 30 букв
     * (кириллица).
     *
     * Срок кредита - целое число, большее или равное 6 месяцам, но не более 120 (10 лет).
     *
     * Дата рождения - число в формате гггг-мм-дд, не позднее 18 лет с текущего дня, на момент
     * окончания кредита не старше 65 лет.
     *
     * Email адрес - строка, подходящая под паттерн
     * ^[a-z0-9A-Z_!#$%&'*+/=?`{|}~^.-]+@[a-z0-9A-Z.-]+$
     *
     * Серия паспорта - 4 цифры, номер паспорта - 6 цифр.
     *
     */
    public static List<String> loanStatementRequestValidate(LoanStatementRequestDto dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getAmount() == null) {
            errors.add("Amount cannot be null");
        } else if (dto.getAmount().compareTo(new BigDecimal("20000")) < 0) {
            errors.add("Loan amount must be at least 20000");
        } else if (dto.getAmount().compareTo(new BigDecimal("5000000")) > 0) {
            errors.add("Loan amount cannot exceed 5000000");
        }

        if (dto.getTerm() == null) {
            errors.add("Term cannot be null");
        } else if (dto.getTerm() < 6) {
            errors.add("Loan term must be at least 6 months");
        } else if (dto.getTerm() > 120) {
            errors.add("Loan term cannot exceed 120 months");
        }

        if (dto.getFirstName() == null || dto.getFirstName().trim().isEmpty()) {
            errors.add("First name is required");
        } else if (!dto.getFirstName().matches("^[а-яА-ЯёЁ]{2,30}$")) {
            errors.add("First name must be 2-30 Cyrillic letters");
        }

        if (dto.getLastName() == null || dto.getLastName().trim().isEmpty()) {
            errors.add("Last name is required");
        } else if (!dto.getLastName().matches("^[а-яА-ЯёЁ]{2,30}$")) {
            errors.add("Last name must be 2-30 Cyrillic letters");
        }

        if (dto.getMiddleName() != null && !dto.getMiddleName().isEmpty()
                && !dto.getMiddleName().matches("^[а-яА-ЯёЁ]{2,30}$")) {
            errors.add("Middle name must be 2-30 Cyrillic letters if provided");
        }

        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            errors.add("Email is required");
        } else if (!dto.getEmail().matches("^[a-z0-9A-Z_!#$%&'*+/=?`{|}~^.-]+@[a-z0-9A-Z.-]+$")) {
            errors.add("Invalid email format");
        }

        if (dto.getBirthDate() == null) {
            errors.add("Birthdate is required");
        } else {
            if (!dto.getBirthDate().isBefore(LocalDate.now())) {
                errors.add("Birthdate must be in the past");
            }

            LocalDate today = LocalDate.now();
            Period age = Period.between(dto.getBirthDate(), today);
            if (age.getYears() < 18) {
                errors.add("You must be at least 18 years old");
            }

            if (age.getYears() + (dto.getTerm() / 12) > 65) {
                errors.add("Age at the end of the loan must not be older than 65 years");
            }
        }

        if (dto.getPassportSeries() == null || dto.getPassportSeries().trim().isEmpty()) {
            errors.add("Passport series is required");
        } else if (!dto.getPassportSeries().matches("^\\d{4}$")) {
            errors.add("Passport series must be 4 digits");
        }

        if (dto.getPassportNumber() == null || dto.getPassportNumber().trim().isEmpty()) {
            errors.add("Passport number is required");
        } else if (!dto.getPassportNumber().matches("^\\d{6}$")) {
            errors.add("Passport number must be 6 digits");
        }

        return errors;
    }

    /*
     * Все поля не должны быть null или пустыми
     *
     * Сумма кредита - действительно число, большее или равное 20000 и меньшее или равное 5000000
     *
     * Имя, Фамилия - от 2 до 30 букв (кириллица). Отчество, при наличии - от 2 до 30 букв
     * (кириллица).
     *
     * Срок кредита - целое число, большее или равное 6 месяцам, но не более 120 (10 лет).
     *
     * Дата рождения - число в формате гггг-мм-дд, не позднее 18 лет с текущего дня, на момент
     * окончания кредита не старше 65 лет.
     *
     * Серия паспорта - 4 цифры, номер паспорта - 6 цифр.
     *
     * Поле "Кем выдан" паспорт - не более 200 символов
     *
     * Сумма иждивенца - положительное число
     *
     * Дата выдачи паспорта - не позднее текущего дня
     */
    public static List<String> scoringDataValidate(ScoringDataDto dto) {
        List<String> errors = DtoValidator.loanStatementRequestValidate(LoanStatementRequestDto
                .builder().amount(dto.getAmount()).term(dto.getTerm()).firstName(dto.getFirstName())
                .lastName(dto.getLastName()).middleName(dto.getMiddleName()).email("test@mail.com")
                .birthDate(dto.getBirthdate()).passportSeries(dto.getPassportSeries())
                .passportNumber(dto.getPassportNumber()).build());

        if (dto.getGender() == null) {
            errors.add("Gender is required");
        }

        if (dto.getPassportIssueDate() == null) {
            errors.add("Passport issue date is required");
        } else if (dto.getPassportIssueDate().isAfter(LocalDate.now())) {
            errors.add("Passport issue date must be in past or present");
        }

        if (dto.getPassportIssueBranch() == null || dto.getPassportIssueBranch().trim().isEmpty()) {
            errors.add("Passport issue branch is required");
        } else if (dto.getPassportIssueBranch().length() > 200) {
            errors.add("Passport issue branch max length is 200 characters");
        }

        if (dto.getMaritalStatus() == null) {
            errors.add("Marital status is required");
        }

        if (dto.getDependentAmount() == null) {
            errors.add("Dependent amount is required");
        } else if (dto.getDependentAmount() <= 0) {
            errors.add("Dependent amount must be positive");
        }

        if (dto.getEmployment() == null) {
            errors.add("Employment data is required");
        } else {
            List<String> employmentErrors = DtoValidator.employmentValidate(dto.getEmployment());
            for (String err : employmentErrors) {
                errors.add("Employment: " + err);
            }
        }

        if (dto.getAccountNumber() == null || dto.getAccountNumber().trim().isEmpty()) {
            errors.add("Account number is required");
        }

        if (dto.getIsInsuranceEnabled() == null) {
            errors.add("Insurance status is required");
        }

        if (dto.getIsSalaryClient() == null) {
            errors.add("Salary client status is required");
        }

        return errors;
    }
}
