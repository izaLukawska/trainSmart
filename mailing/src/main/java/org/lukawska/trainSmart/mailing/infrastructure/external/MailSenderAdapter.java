package org.lukawska.trainSmart.mailing.infrastructure.external;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainSmart.mailing.application.dto.MailRequest;
import org.lukawska.trainSmart.mailing.application.service.MailSender;
import org.lukawska.trainSmart.mailing.domain.valueObject.Attachment;
import org.lukawska.trainSmart.mailing.infrastructure.config.MailingProperties;
import org.slf4j.MDC;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailSenderAdapter implements MailSender {

    private final JavaMailSender mailSender;

    private final MailingProperties mailingProperties;

    @Override
    @Retryable(retryFor = {MailException.class}, backoff = @Backoff(delay = 5000))
    public void sendEmail(MailRequest mailRequest) throws MessagingException {
        String correlationId = MDC.get("correlationId");
        log.debug("Sending email with correlation id: {}", correlationId);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = createMimeMessageHelper(mailRequest, message);

        log.debug("Applying mail data for correlation id: {}", correlationId);
        applyMailData(mailRequest, messageHelper);

        mailSender.send(message);
        log.debug("Successfully send email with correlation id: {}", correlationId);
    }

    private MimeMessageHelper createMimeMessageHelper(MailRequest mailRequest, MimeMessage message)
            throws MessagingException {
        boolean isMultipart = !mailRequest.attachments().isEmpty();
        return new MimeMessageHelper(message, isMultipart, "UTF-8");
    }

    private void applyMailData(MailRequest mailRequest, MimeMessageHelper helper) throws MessagingException {
        helper.setTo(mailRequest.recipients().toArray(new String[0]));
        helper.setSubject(mailRequest.subject());
        helper.setText(mailRequest.text(), mailRequest.isHtml());
        helper.setFrom(mailingProperties.getFrom());
        helper.setReplyTo(mailingProperties.getReplyTo());
        helper.setCc(mailRequest.cc().toArray(String[]::new));
        helper.setBcc(mailRequest.bcc().toArray(String[]::new));

        addAttachments(mailRequest, helper);

    }

    private void addAttachments(MailRequest mailRequest, MimeMessageHelper helper) throws MessagingException {
        for (Attachment attachment : mailRequest.attachments()) {
            ByteArrayResource resource = new ByteArrayResource(attachment.getContent());
            helper.addAttachment(attachment.getFileName(), resource);
        }
    }
}
