package org.lukawska.trainSmart.mailing.infrastructure.external;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainSmart.mailing.application.dto.Attachment;
import org.lukawska.trainSmart.mailing.application.dto.MailRequest;
import org.lukawska.trainSmart.mailing.application.service.MailService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class JavaMailSenderAdapter implements MailService {

	private final JavaMailSender mailSender;

	@Override
	public void sendEmail(MailRequest mailRequest) throws MessagingException {
		if (mailRequest == null) {
			throw new IllegalArgumentException("MailRequest cannot be null.");
		}

		if (mailRequest.getTo() == null || mailRequest.getTo().length == 0) {
			log.warn("No recipient found in the mail request: {}", mailRequest);
			throw new IllegalArgumentException("No recipient found.");
		}

		log.info("Sending email to: {}", Arrays.toString(mailRequest.getTo()));

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper messageHelper = createMimeMessageHelper(mailRequest, message);

		applyMailData(mailRequest, messageHelper);
		addAttachments(mailRequest, messageHelper);

		mailSender.send(message);
		log.info("Email send to: {}", Arrays.toString(mailRequest.getTo()));
	}

	private MimeMessageHelper createMimeMessageHelper(MailRequest mailRequest, MimeMessage message)
			throws MessagingException {
		boolean isMultipart = mailRequest.isHtml() || !mailRequest.getAttachments().isEmpty();
		return new MimeMessageHelper(message, isMultipart, "UTF-8");
	}

	private void applyMailData(MailRequest mailRequest, MimeMessageHelper helper) throws MessagingException {
		helper.setTo(mailRequest.getTo());
		helper.setSubject(mailRequest.getSubject());
		helper.setText(mailRequest.getText(), mailRequest.isHtml());
	}

	private void addAttachments(MailRequest mailRequest, MimeMessageHelper helper) throws MessagingException {
		for (Attachment attachment : mailRequest.getAttachments()) {
			helper.addAttachment(attachment.fileName(), attachment.source(), attachment.mimeType());
		}
	}
}
