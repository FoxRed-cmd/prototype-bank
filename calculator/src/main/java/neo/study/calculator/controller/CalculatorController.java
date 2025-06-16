package neo.study.calculator.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import neo.study.calculator.dto.CreditDto;
import neo.study.calculator.dto.LoanOfferDto;
import neo.study.calculator.dto.LoanStatementRequestDto;
import neo.study.calculator.dto.ScoringDataDto;
import neo.study.calculator.service.CalculatorService;
import neo.study.calculator.utils.exception.NotValidException;
import neo.study.calculator.utils.validation.DtoValidator;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/calculator")
@RequiredArgsConstructor
public class CalculatorController {
    private final CalculatorService calculatorService;

    @PostMapping("/offers")
    public ResponseEntity<List<LoanOfferDto>> offers(
            @RequestBody LoanStatementRequestDto loanStatementData) {

        var errors = DtoValidator.loanStatementRequestValidate(loanStatementData);
        if (!errors.isEmpty()) {
            throw new NotValidException(errors, "Validation error");
        }
        return ResponseEntity.ok(calculatorService.getPrescoringResults(loanStatementData));
    }

    @PostMapping("/calc")
    public ResponseEntity<CreditDto> calc(@RequestBody ScoringDataDto scoringData) {
        var errors = DtoValidator.scoringDataValidate(scoringData);
        if (!errors.isEmpty()) {
            throw new NotValidException(errors, "Validation error");
        }
        return ResponseEntity.ok(calculatorService.getScoringResult(scoringData));
    }

}
