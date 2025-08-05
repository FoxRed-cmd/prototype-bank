package neo.study.statement.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import neo.study.statement.dto.LoanOfferDto;
import neo.study.statement.dto.LoanStatementRequestDto;
import neo.study.statement.service.StatementService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/statement")
@RequiredArgsConstructor
public class StatementController {
    private final StatementService statementService;

    @PostMapping
    public ResponseEntity<List<LoanOfferDto>> prescoring(@RequestBody LoanStatementRequestDto request) {
        return ResponseEntity.ok(statementService.processStatement(request));
    }

    @PostMapping("/offer")
    public void selectOffer(@RequestBody LoanOfferDto selectedOffer) {
        statementService.selectOffer(selectedOffer);
    }
}
