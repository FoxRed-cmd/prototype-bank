package neo.study.deal.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import neo.study.deal.dto.FinishRegistrationRequestDto;
import neo.study.deal.dto.LoanOfferDto;
import neo.study.deal.dto.LoanStatementRequestDto;
import neo.study.deal.service.DealService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/deal")
@RequiredArgsConstructor
public class DealController {
    private final DealService dealService;

    @PostMapping("/statement")
    public ResponseEntity<List<LoanOfferDto>> statementProcessing(
            @RequestBody LoanStatementRequestDto request) {
        return ResponseEntity.ok(dealService.statementProcessing(request));
    }

    @PostMapping("/offer/select")
    public void selectOffer(@RequestBody LoanOfferDto requestOffer) {
        dealService.selectOffer(requestOffer);
    }

    @PostMapping("/calculate/{statementId}")
    public void registrationComplete(@PathVariable String statementId,
            @RequestBody FinishRegistrationRequestDto requestRegistration) {
        dealService.finishRegistration(statementId, requestRegistration);
    }



}
