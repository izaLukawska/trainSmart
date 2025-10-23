package org.lukawska.trainSmart.mailing.application.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainSmart.mailing.application.dto.MailRequest;
import org.lukawska.trainSmart.mailing.application.dto.MailResponse;
import org.lukawska.trainSmart.mailing.application.exception.ExceptionType;
import org.lukawska.trainSmart.mailing.application.exception.MailingException;
import org.lukawska.trainSmart.mailing.application.mapper.MailMapper;
import org.lukawska.trainSmart.mailing.application.validation.AttachmentValidator;
import org.lukawska.trainSmart.mailing.domain.entities.MailEntity;
import org.lukawska.trainSmart.mailing.domain.repository.MailRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final MailRepository mailRepository;

    private final MailSender mailSender;

    private final AttachmentValidator validator;

    @Value("${mail.from}")
    private String mailFrom;

    @Value("${mail.reply-to}")
    private String replyTo;

    public MailResponse sendMail(MailRequest mailRequest) {
        String correlationId = UUID.randomUUID().toString();
        if (!CollectionUtils.isEmpty(mailRequest.attachments())) {
            log.info("Validating attachments for correlationId: {}", correlationId);
            validator.validateAttachments(mailRequest.attachments());
        }

        MailEntity mailEntity = MailMapper.mapToEntity(mailRequest);
        try {
            mailSender.sendEmail(mailRequest, correlationId);
            mailEntity.markAsSent();
            mailRepository.save(mailEntity);
            return MailMapper.mapToResponse(mailEntity, mailFrom, replyTo);
        } catch (MessagingException e) {
            log.error("Error occurred while sending email for correlationId: {}", correlationId, e);
            throw new MailingException(ExceptionType.MAIL_SEND_ERROR);
        }
    }

    public MailResponse getMailResponseById(Long id) {
        return mailRepository.findById(id)
                             .map(m -> MailMapper.mapToResponse(m, mailFrom, replyTo))
                             .orElseThrow(() -> new MailingException(ExceptionType.MAIL_NOT_FOUND));
    }

    public List<MailResponse> getAllMails() {
        return mailRepository.findAll()
                             .stream()
                             .map(m -> MailMapper.mapToResponse(m, mailFrom, replyTo))
                             .toList();
    }

    public List<MailResponse> getAllMailsByRecipient(String recipient) {
        return mailRepository.findAllByRecipient(recipient)
                             .stream()
                             .map(m -> MailMapper.mapToResponse(m, mailFrom, replyTo))
                             .toList();
    }

    public List<MailResponse> getAllMailsBySubjectContaining(String keyword) {
        return mailRepository.findAllBySubjectContaining(keyword)
                             .stream()
                             .map(m -> MailMapper.mapToResponse(m, mailFrom, replyTo))
                             .toList();
    }
}
