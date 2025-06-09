package neo.study.calculator.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import neo.study.calculator.dto.CreditDto;
import neo.study.calculator.dto.LoanOfferDto;
import neo.study.calculator.dto.LoanStatementRequestDto;
import neo.study.calculator.dto.ScoringDataDto;
import neo.study.calculator.service.CalculatorService;
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
            @Valid @RequestBody LoanStatementRequestDto loanStatementData) {

        return ResponseEntity.ok(calculatorService.getPrescoringResults(loanStatementData));
    }

    @PostMapping("/calc")
    public ResponseEntity<CreditDto> calc(@Valid @RequestBody ScoringDataDto scoringData) {
        return ResponseEntity.ok(calculatorService.getScoringResult(scoringData));
    }

}
