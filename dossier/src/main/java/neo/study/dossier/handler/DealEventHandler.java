package neo.study.dossier.handler;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import neo.study.deal.dto.EmailMessage;

@Component
@Slf4j
public class DealEventHandler {

    @KafkaListener(topics = "finish-registration")
    public void finishRegistrationHandler(EmailMessage emailMessage) {

        log.info("Received email message: {}", emailMessage);
    }
}
