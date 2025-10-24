package org.lukawska.trainSmart.mailing.application.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lukawska.trainSmart.mailing.application.dto.MailRequest;
import org.lukawska.trainSmart.mailing.application.dto.MailResponse;
import org.lukawska.trainSmart.mailing.application.exception.ExceptionType;
import org.lukawska.trainSmart.mailing.application.exception.MailingException;
import org.lukawska.trainSmart.mailing.application.mapper.MailMapper;
import org.lukawska.trainSmart.mailing.domain.entities.MailEntity;
import org.lukawska.trainSmart.mailing.domain.repository.MailRepository;
import org.lukawska.trainSmart.mailing.infrastructure.config.MailingProperties;
import org.lukawska.trainSmart.mailing.infrastructure.external.AttachmentValidatorAdapter;
import org.slf4j.MDC;
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

    private final AttachmentValidatorAdapter attachmentValidatorAdapter;

    private final MailingProperties mailingProperties;

    public MailResponse sendMail(MailRequest mailRequest) {
        String correlationId = MDC.get("correlationId");
        if (StringUtils.isBlank(correlationId)) {
            correlationId = UUID.randomUUID().toString();
        }

        if (!CollectionUtils.isEmpty(mailRequest.attachments())) {
            log.info("Validating attachments for correlationId: {}", correlationId);
            attachmentValidatorAdapter.validateAttachments(mailRequest.attachments());
        }

        try {
            mailSender.sendEmail(mailRequest);
            MailEntity mailEntity = MailMapper.mapToEntity(mailRequest);
            mailEntity.markAsSent();
            mailRepository.save(mailEntity);
            return MailMapper.mapToResponse(mailEntity,
                                            mailingProperties.getFrom(),
                                            mailingProperties.getReplyTo());
        } catch (MessagingException e) {
            log.error("Error occurred while sending email for correlationId: {}", correlationId, e);
            throw new MailingException(ExceptionType.MAIL_SEND_ERROR);
        }
    }

    public MailResponse getMailResponseById(Long id) {
        return mailRepository.findById(id)
                             .map(mail -> MailMapper.mapToResponse(mail,
                                                                   mailingProperties.getFrom(),
                                                                   mailingProperties.getReplyTo()))
                             .orElseThrow(() -> new MailingException(ExceptionType.MAIL_NOT_FOUND));
    }

    public List<MailResponse> getAllMails() {
        return mailRepository.findAll()
                             .stream()
                             .map(mail -> MailMapper.mapToResponse(mail,
                                                                   mailingProperties.getFrom(),
                                                                   mailingProperties.getReplyTo()))
                             .toList();
    }

    public List<MailResponse> getAllMailsByRecipient(String recipient) {
        return mailRepository.findAllByRecipient(recipient)
                             .stream()
                             .map(mail -> MailMapper.mapToResponse(mail,
                                                                   mailingProperties.getFrom(),
                                                                   mailingProperties.getReplyTo()))
                             .toList();
    }

    public List<MailResponse> getAllMailsBySubjectContaining(String keyword) {
        return mailRepository.findAllBySubjectContaining(keyword)
                             .stream()
                             .map(mail -> MailMapper.mapToResponse(mail,
                                                                   mailingProperties.getFrom(),
                                                                   mailingProperties.getReplyTo()))
                             .toList();
    }
}
