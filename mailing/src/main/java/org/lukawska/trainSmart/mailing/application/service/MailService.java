package org.lukawska.trainSmart.mailing.application.service;

import jakarta.mail.MessagingException;
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
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

	private final MailRepository mailRepository;

	private final MailSender mailSender;

	private final MailMapper mailMapper;

	private final AttachmentValidation attachmentValidation;

	public MailResponse sendMail(MailRequest mailRequest) {
		String correlationId = UUID.randomUUID().toString();
		if (!CollectionUtils.isEmpty(mailRequest.attachments())) {
			log.info("Validating attachments for correlationId: {}", correlationId);
			attachmentValidation.validateAttachments(mailRequest.attachments());
		}

		MailEntity mailEntity = mailMapper.mapToEntity(mailRequest);
		try {
			mailSender.sendEmail(mailRequest, correlationId);
			mailEntity.markAsSent();
			mailRepository.save(mailEntity);
			return mailMapper.mapToResponse(mailEntity);
		} catch (MessagingException e) {
			log.error("Error occurred while sending email for correlationId: {}", correlationId, e);
			throw new RestException(ExceptionType.MAIL_SEND_ERROR);
		}
	}

	public MailResponse getMailById(Long id){
		return mailRepository.findById(id)
				.map(mailMapper::mapToResponse)
				.orElseThrow(() -> new RestException(ExceptionType.MAIL_NOT_FOUND));
	}

	public List<MailResponse> getAllMails(){
		return mailRepository.findAll()
				.stream()
				.map(mailMapper::mapToResponse)
				.toList();
	}

	public List<MailResponse> getAllMailsByRecipient(String recipient){
		return mailRepository.findAllByRecipient(recipient)
				.stream()
				.map(mailMapper::mapToResponse)
				.toList();
	}

	public List<MailResponse> getAllMailsByCc(String cc){
		return mailRepository.findAllByCcContaining(cc)
				.stream()
				.map(mailMapper::mapToResponse)
				.toList();
	}

	public List<MailResponse> getAllMailsByBcc(String bcc){
		return mailRepository.findAllByBccContaining(bcc)
				.stream()
				.map(mailMapper::mapToResponse)
				.toList();
	}

	public List<MailResponse> getAllMailsBySentAtBetween(String from, String to){
		return mailRepository.findAllBySentAtBetween(Instant.parse(from), Instant.parse(to))
				.stream()
				.map(mailMapper::mapToResponse)
				.toList();
	}

	public List<MailResponse> getAllMailsBySubjectContaining(String keyword){
		return mailRepository.findAllBySubjectContaining(keyword)
				.stream()
				.map(mailMapper::mapToResponse)
				.toList();
	}

	public List<MailResponse> getAllMailsByIsHtml(boolean isHtml){
		return mailRepository.findAllByIsHtml(isHtml)
				.stream()
				.map(mailMapper::mapToResponse)
				.toList();
	}
}
