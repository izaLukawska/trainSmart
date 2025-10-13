package org.lukawska.trainSmart.mailing.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainSmart.mailing.application.dto.MailRequest;
import org.lukawska.trainSmart.mailing.application.dto.MailResponse;
import org.lukawska.trainSmart.mailing.application.exception.ExceptionType;
import org.lukawska.trainSmart.mailing.application.exception.RestException;
import org.lukawska.trainSmart.mailing.application.mapper.MailMapper;
import org.lukawska.trainSmart.mailing.application.validation.AttachmentValidation;
import org.lukawska.trainSmart.mailing.domain.entities.MailEntity;
import org.lukawska.trainSmart.mailing.domain.repository.MailRepository;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

	private final MailRepository mailRepository;

	private final MailSender mailSender;

	private final AttachmentValidation attachmentValidation;

	public MailResponse sendMail(MailRequest mailRequest) {
		String correlationId = UUID.randomUUID().toString();
		log.debug("Sending email to: {} with correlationId: {}", mailRequest.recipients(), correlationId);
		MailEntity mailEntity = MailMapper.mapToEntity(mailRequest);
		try {
			log.info("Validating attachments for mail with correlation id: {}", correlationId);
			attachmentValidation.validateAttachments(mailEntity.getAttachmentList());
			mailSender.sendEmail(mailRequest, correlationId);
			log.info("Successfully sent mail with correlationId: {}", correlationId);
			mailEntity.markAsSent();
			return MailMapper.mapToResponse(mailEntity);
		} catch (RestException e) {
			log.error("Application error for mail {}: {}", correlationId, e.getMessage());
			throw e;
		} catch (MailAuthenticationException e) {
			log.error("Mail authentication failed for mail {}: {}", correlationId, e.getMessage());
			throw new RestException(ExceptionType.MAIL_AUTH_ERROR);
		} catch (Exception e) {
			log.error("Failed to send mail with correlation id: {}. Error: {}", correlationId, e.getMessage(), e);
			throw new RestException(ExceptionType.MAIL_SEND_ERROR);
		} finally {
			mailRepository.save(mailEntity);
		}
	}
}
