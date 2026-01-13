package org.lukawska.trainsmart.mailing.infrastructure.external;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.mailing.application.dto.MailDetails;
import org.lukawska.trainsmart.mailing.application.service.MailSender;
import org.lukawska.trainsmart.mailing.domain.valueObjects.Attachment;
import org.lukawska.trainsmart.mailing.infrastructure.config.MailingProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailSenderAdapter implements MailSender {

    private final JavaMailSender mailSender;

    private final MailingProperties mailingProperties;

    @Override
    @Retryable(retryFor = {MailException.class}, backoff = @Backoff(delay = 5000))
    public void sendEmail(MailDetails mailDetails) throws MessagingException {
        log.debug("Attempting to send mail");

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = createMimeMessageHelper(mailDetails, message);
        applyMailData(mailDetails, messageHelper);

        mailSender.send(message);
        log.info("Mail sent successfully.");
    }

    private MimeMessageHelper createMimeMessageHelper(MailDetails mailDetails, MimeMessage message)
            throws MessagingException {
        boolean isMultipart = !CollectionUtils.isEmpty(mailDetails.attachments());
        return new MimeMessageHelper(message, isMultipart, "UTF-8");
    }

    private void applyMailData(MailDetails mailDetails, MimeMessageHelper helper) throws MessagingException {
        helper.setTo(mailDetails.recipients().toArray(String[]::new));
        helper.setSubject(mailDetails.subject());
        helper.setText(mailDetails.text(), mailDetails.isHtml());
        helper.setFrom(mailingProperties.getFrom());
        helper.setReplyTo(mailingProperties.getReplyTo());

        if (!CollectionUtils.isEmpty(mailDetails.cc())) {
            helper.setCc(mailDetails.cc().toArray(String[]::new));
        }

        if (!CollectionUtils.isEmpty(mailDetails.bcc())) {
            helper.setCc(mailDetails.bcc().toArray(String[]::new));
        }

        if (!CollectionUtils.isEmpty(mailDetails.attachments())) {
            addAttachments(mailDetails, helper);
        }
    }

    private void addAttachments(MailDetails mailDetails, MimeMessageHelper helper) throws MessagingException {
        for (Attachment attachment : mailDetails.attachments()) {
            ByteArrayResource resource = new ByteArrayResource(attachment.getContent());
            helper.addAttachment(attachment.getFileName(), resource);
        }
    }
}
