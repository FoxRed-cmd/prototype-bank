package neo.study.deal.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import neo.study.deal.dto.ApplicationStatus;
import neo.study.deal.dto.FinishRegistrationRequestDto;
import neo.study.deal.dto.LoanOfferDto;
import neo.study.deal.dto.LoanStatementRequestDto;
import neo.study.deal.dto.StatementDto;
import neo.study.deal.service.DealService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/deal")
@RequiredArgsConstructor
public class DealController {
    private final DealService dealService;

    @PostMapping("/statement")
    public ResponseEntity<List<LoanOfferDto>> processStatement(
            @RequestBody LoanStatementRequestDto request) {
        return ResponseEntity.ok(dealService.processStatement(request));
    }

    @PostMapping("/offer/select")
    public void selectOffer(@RequestBody LoanOfferDto requestOffer) {
        dealService.selectOffer(requestOffer);
    }

    @PostMapping("/calculate/{statementId}")
    public void completeRegistration(@PathVariable String statementId,
            @RequestBody FinishRegistrationRequestDto requestRegistration) {
        dealService.finishRegistration(statementId, requestRegistration);
    }

    @PostMapping("/document/{statementId}/send")
    public void sendDocuments(@PathVariable String statementId) {
        dealService.sendDocuments(statementId);
    }

    @PostMapping("/document/{statementId}/sign")
    public void signDocuments(@PathVariable String statementId) {
        dealService.signDocuments(statementId);
    }

    @PostMapping("/document/{statementId}/code")
    public void codeDocuments(@PathVariable String statementId) {
        dealService.codeDocuments(statementId);
    }

    @GetMapping("/admin/statement/{statementId}")
    public ResponseEntity<StatementDto> getStatement(@PathVariable String statementId) {
        return ResponseEntity.ok(dealService.getStatement(statementId));
    }

    @PutMapping("/admin/statement/{statementId}/status")
    public ResponseEntity<StatementDto> updateStatementStatus(@PathVariable String statementId,
            @RequestBody ApplicationStatus status) {

        return ResponseEntity.ok(dealService.updateStatementStatus(statementId, status));
    }
}
