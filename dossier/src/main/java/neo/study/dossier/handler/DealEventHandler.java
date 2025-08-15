package neo.study.dossier.handler;

import java.util.Optional;

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

    @KafkaListener(topics = { "finish-registration", "send-documents", "send-ses", "credit-issued" })
    public void finishRegistrationHandler(EmailMessage emailMessage) {
        log.info("Received email message: {}", emailMessage);

        mailSenderService.sendEmail(emailMessage.getAddress(), getTheme(emailMessage),
                emailMessage.getText());
    }

    private String getTheme(EmailMessage emailMessage) {
        return Optional.ofNullable(emailMessage.getTheme())
                .map(Enum::name)
                .orElse("Default Subject");
    }
}
