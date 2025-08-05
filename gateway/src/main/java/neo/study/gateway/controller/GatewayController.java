package neo.study.gateway.controller;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import neo.study.gateway.dto.FinishRegistrationRequestDto;
import neo.study.gateway.dto.LoanOfferDto;
import neo.study.gateway.dto.LoanStatementRequestDto;
import neo.study.gateway.service.GatewayService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
public class GatewayController {
    private final GatewayService gatewayService;

    @PostMapping("/statement")
    public ResponseEntity<List<LoanOfferDto>> processStatement(@RequestBody LoanStatementRequestDto requestDto) {
        return ResponseEntity.ok(gatewayService.processStatement(requestDto));
    }

    @PostMapping("/statement/offer")
    public void selectOffer(@RequestBody LoanOfferDto requestDto) {
        gatewayService.selectOffer(requestDto);
    }

    @PostMapping("/deal/calculate/{statementId}")
    public void completeRegistration(@PathVariable String statementId,
            @RequestBody FinishRegistrationRequestDto requestRegistration) {
        gatewayService.completeRegistration(statementId, requestRegistration);
    }

    @PostMapping("/deal/document/{statementId}/send")
    public void sendDocuments(@PathVariable String statementId) {
        gatewayService.sendDocuments(statementId);
    }

    @PostMapping("/deal/document/{statementId}/sign")
    public void signDocuments(@PathVariable String statementId) {
        gatewayService.signDocuments(statementId);
    }

    @PostMapping("/deal/document/{statementId}/code")
    public void codeDocuments(@PathVariable String statementId) {
        gatewayService.codeDocuments(statementId);
    }

}
