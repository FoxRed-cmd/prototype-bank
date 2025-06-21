package neo.study.deal.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import neo.study.deal.dto.LoanStatementRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/deal")
public class DealController {

    @PostMapping("/statement")
    public ResponseEntity<String> statementProcessing(
            @RequestBody LoanStatementRequestDto request) {
        return ResponseEntity.ok(request.getFirstName());
    }

    @PostMapping("/offer/select")
    public String selectOffer(@RequestBody String entity) {
        // TODO: process POST request

        return entity;
    }

    @PostMapping("/calculate/{statementId}")
    public String registrationComplete(@RequestBody String entity) {
        // TODO: process POST request

        return entity;
    }



}
