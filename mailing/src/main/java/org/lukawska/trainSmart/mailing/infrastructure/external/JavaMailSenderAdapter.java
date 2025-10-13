package org.lukawska.trainSmart.mailing.infrastructure.external;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainSmart.mailing.application.dto.MailRequest;
import org.lukawska.trainSmart.mailing.domain.entities.Attachment;
import org.lukawska.trainSmart.mailing.application.service.MailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JavaMailSenderAdapter implements MailSender {

	private final JavaMailSender mailSender;

	@Value("${mail.from}")
	private String mailFrom;

	@Value("${mail.reply-to-support}")
	private String replyToSupport;

	@Override
	@Retryable(retryFor = {MailException.class}, backoff = @Backoff(delay = 5000))
	public void sendEmail(MailRequest mailRequest, String correlationId) throws MessagingException {
		log.debug("Sending email with correlation id: {}", correlationId);

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper messageHelper = createMimeMessageHelper(mailRequest, message);

		applyMailData(mailRequest, messageHelper);

		mailSender.send(message);
		log.debug("Successfully send email with correlation id: {}", correlationId);
	}

	private MimeMessageHelper createMimeMessageHelper(MailRequest mailRequest, MimeMessage message)
			throws MessagingException {
		boolean isMultipart = !mailRequest.attachmentList().isEmpty();
		return new MimeMessageHelper(message, isMultipart, "UTF-8");
	}

	private void applyMailData(MailRequest mailRequest, MimeMessageHelper helper) throws MessagingException {
		helper.setTo(mailRequest.recipients().toArray(new String[0]));
		helper.setSubject(mailRequest.subject());
		helper.setText(mailRequest.body(), mailRequest.isHtml());
		helper.setFrom(mailFrom);
		helper.setReplyTo(replyToSupport);

		if (!mailRequest.cc().isEmpty()) {
			helper.setCc(mailRequest.cc().toArray(new String[0]));
		}

		if (!mailRequest.bcc().isEmpty()) {
			helper.setBcc(mailRequest.bcc().toArray(new String[0]));
		}

		if (!mailRequest.attachmentList().isEmpty()) {
			addAttachments(mailRequest, helper);
		}
	}

	private void addAttachments(MailRequest mailRequest, MimeMessageHelper helper) throws MessagingException {
		for (Attachment attachment : mailRequest.attachmentList()) {
			helper.addAttachment(attachment.fileName(), attachment.source(), attachment.mimeType());
		}
	}
}
