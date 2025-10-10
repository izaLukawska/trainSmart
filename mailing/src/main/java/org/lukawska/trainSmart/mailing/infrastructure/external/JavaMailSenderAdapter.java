package org.lukawska.trainSmart.mailing.infrastructure.external;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lukawska.trainSmart.mailing.application.dto.Attachment;
import org.lukawska.trainSmart.mailing.application.dto.MailRequest;
import org.lukawska.trainSmart.mailing.application.service.MailService;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class JavaMailSenderAdapter implements MailService {

	private final JavaMailSender mailSender;

	@Override
	@Retryable(retryFor = {MailException.class}, backoff = @Backoff(delay = 5000))
	public void sendEmail(MailRequest mailRequest) throws MessagingException {
		if (mailRequest == null) {
			log.error("MailRequest cannot be null.");
			throw new IllegalArgumentException("MailRequest cannot be null.");
		}

		if(Arrays.stream(mailRequest.getRecipients()).anyMatch(StringUtils::isBlank)) {
			log.error("Wrong recipients address in request: {}", mailRequest);
			throw new IllegalArgumentException("Recipient address cannot be blank.");
		}

		log.debug("Sending email to: {}", Arrays.toString(mailRequest.getRecipients()));

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper messageHelper = createMimeMessageHelper(mailRequest, message);

		applyMailData(mailRequest, messageHelper);

		if (!mailRequest.getAttachments().isEmpty()) {
			addAttachments(mailRequest, messageHelper);
		}

		mailSender.send(message);
		log.debug("Email sent to: {}", Arrays.toString(mailRequest.getRecipients()));
	}

	private MimeMessageHelper createMimeMessageHelper(MailRequest mailRequest, MimeMessage message)
			throws MessagingException {
		boolean isMultipart = !mailRequest.getAttachments().isEmpty();
		return new MimeMessageHelper(message, isMultipart, "UTF-8");
	}

	private void applyMailData(MailRequest mailRequest, MimeMessageHelper helper) throws MessagingException {
		helper.setTo(mailRequest.getRecipients());
		helper.setSubject(mailRequest.getSubject());
		helper.setText(mailRequest.getText(), mailRequest.isHtml());
	}

	private void addAttachments(MailRequest mailRequest, MimeMessageHelper helper) throws MessagingException {
		for (Attachment attachment : mailRequest.getAttachments()) {
			helper.addAttachment(attachment.fileName(), attachment.source(), attachment.mimeType());
		}
	}
}
