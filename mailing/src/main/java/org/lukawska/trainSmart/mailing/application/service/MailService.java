package org.lukawska.trainSmart.mailing.application.service;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainSmart.mailing.application.dto.MailRequest;
import org.lukawska.trainSmart.mailing.application.dto.MailResponse;
import org.lukawska.trainSmart.mailing.application.exception.ExceptionType;
import org.lukawska.trainSmart.mailing.application.exception.MailingException;
import org.lukawska.trainSmart.mailing.domain.entities.MailEntity;
import org.lukawska.trainSmart.mailing.domain.repository.MailRepository;
import org.lukawska.trainSmart.mailing.infrastructure.config.MailingProperties;
import org.lukawska.trainSmart.mailing.infrastructure.external.AttachmentValidatorAdapter;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.lukawska.trainSmart.mailing.application.mapper.MailMapper.mapToEntity;
import static org.lukawska.trainSmart.mailing.application.mapper.MailMapper.mapToResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final MailRepository mailRepository;

    private final MailSender mailSender;

    private final AttachmentValidatorAdapter attachmentValidatorAdapter;

    private final MailingProperties mailingProperties;

    @Transactional
    public MailResponse sendMail(MailRequest mailRequest) {
        if (!CollectionUtils.isEmpty(mailRequest.attachments())) {
            attachmentValidatorAdapter.validateAttachments(mailRequest.attachments());
        }

        try {
            mailSender.sendEmail(mailRequest);
            MailEntity mailEntity = mapToEntity(mailRequest);
            mailRepository.save(mailEntity);
            log.info("Saved mail with ID: {}", mailEntity.getId());
            return mapToResponse(mailEntity, mailingProperties.getFrom(), mailingProperties.getReplyTo());
        } catch (MessagingException | MailException e) {
            log.error("Error occurred while sending email {}", e.getMessage());
            throw new MailingException(ExceptionType.MAIL_SEND_ERROR);
        }
    }

    @Transactional
    public MailResponse getMailResponseById(Long id) {
        log.info("Getting email for ID: {}", id);
        return mailRepository
                .findById(id)
                .map(mail -> mapToResponse(mail, mailingProperties.getFrom(), mailingProperties.getReplyTo()))
                .orElseThrow(() -> new MailingException(ExceptionType.MAIL_NOT_FOUND));
    }

    @Transactional
    public List<MailResponse> getAllMailsByRecipient(String recipient) {
        List<MailEntity> foundMails = mailRepository.findAllByRecipient(recipient);
        log.debug("Found {} mails for recipient {}", foundMails.size(), recipient);

        return foundMails.stream()
                         .map(mail -> mapToResponse(mail, mailingProperties.getFrom(), mailingProperties.getReplyTo()))
                         .toList();
    }

    @Transactional
    public List<MailResponse> getAllMailsBySubjectContaining(String keyword) {
        List<MailEntity> foundMails = mailRepository.findAllBySubjectContaining(keyword);
        log.debug("Found {} mails for keyword {}", foundMails.size(), keyword);

        return foundMails.stream()
                         .map(mail -> mapToResponse(mail, mailingProperties.getFrom(), mailingProperties.getReplyTo()))
                         .toList();
    }
}
