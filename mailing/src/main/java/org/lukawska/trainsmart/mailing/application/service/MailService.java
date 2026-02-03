package org.lukawska.trainsmart.mailing.application.service;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.mailing.application.dto.MailDetails;
import org.lukawska.trainsmart.mailing.application.dto.MailResponse;
import org.lukawska.trainsmart.mailing.application.exception.ExceptionType;
import org.lukawska.trainsmart.mailing.application.exception.MailingException;
import org.lukawska.trainsmart.mailing.domain.entities.MailEntity;
import org.lukawska.trainsmart.mailing.domain.repositories.MailRepository;
import org.lukawska.trainsmart.mailing.infrastructure.config.MailingProperties;
import org.lukawska.trainsmart.mailing.infrastructure.external.AttachmentValidatorAdapter;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static org.lukawska.trainsmart.mailing.application.mapper.MailMapper.mapToEntity;
import static org.lukawska.trainsmart.mailing.application.mapper.MailMapper.mapToResponse;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class MailService {

    private final MailRepository mailRepository;

    private final MailSender mailSender;

    private final AttachmentValidatorAdapter attachmentValidatorAdapter;

    private final MailingProperties mailingProperties;

    @Transactional
    public MailResponse sendMail(@Valid MailDetails mailDetails) {
        if (!CollectionUtils.isEmpty(mailDetails.attachments())) {
            log.info("Validating {} attachments.", mailDetails.attachments().size());
            attachmentValidatorAdapter.validateAttachments(mailDetails.attachments());
        }

        try {
            mailSender.sendEmail(mailDetails);
            MailEntity mailEntity = mapToEntity(mailDetails);
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
        MailEntity foundMail = mailRepository.findById(id)
                                             .orElseThrow(() -> new MailingException(ExceptionType.MAIL_NOT_FOUND));
        log.info("Mail with ID: {} found.", id);

        return mapToResponse(foundMail, mailingProperties.getFrom(), mailingProperties.getReplyTo());
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
