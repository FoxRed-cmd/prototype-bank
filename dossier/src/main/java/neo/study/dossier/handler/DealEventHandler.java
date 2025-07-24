package neo.study.dossier.handler;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import neo.study.deal.dto.EmailMessage;
import neo.study.dossier.service.MailSenderService;

@Slf4j
@Component
@RequiredArgsConstructor
public class DealEventHandler {
    private final MailSenderService mailSenderService;

    @KafkaListener(topics = "finish-registration")
    public void finishRegistrationHandler(EmailMessage emailMessage) {
        log.info("Received email message: {}", emailMessage);

        mailSenderService.sendEmail(emailMessage.getAddress(), emailMessage.getTheme().name(),
                emailMessage.getText());
    }

    @KafkaListener(topics = "send-documents")
    public void sendDocumentsHandler(EmailMessage emailMessage) {
        log.info("Received email message: {}", emailMessage);

        mailSenderService.sendEmail(emailMessage.getAddress(), emailMessage.getTheme().name(),
                emailMessage.getText());
    }

    @KafkaListener(topics = "send-ses")
    public void sendSESHandler(EmailMessage emailMessage) {
        log.info("Received email message: {}", emailMessage);

        mailSenderService.sendEmail(emailMessage.getAddress(), emailMessage.getTheme().name(),
                emailMessage.getText());
    }

    @KafkaListener(topics = "credit-issued")
    public void creditIssuedHandler(EmailMessage emailMessage) {
        log.info("Received email message: {}", emailMessage);

        mailSenderService.sendEmail(emailMessage.getAddress(), emailMessage.getTheme().name(),
                emailMessage.getText());
    }
}
