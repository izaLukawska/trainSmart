package org.lukawska.trainsmart.mailing.application.service;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.mailing.application.dto.AttachmentMeta;
import org.lukawska.trainsmart.mailing.application.dto.MailDetails;
import org.lukawska.trainsmart.mailing.application.dto.MailResponse;
import org.lukawska.trainsmart.mailing.application.exception.ExceptionType;
import org.lukawska.trainsmart.mailing.application.exception.MailingException;
import org.lukawska.trainsmart.mailing.domain.entities.MailEntity;
import org.lukawska.trainsmart.mailing.domain.repositories.MailRepository;
import org.lukawska.trainsmart.mailing.domain.valueObjects.Attachment;
import org.lukawska.trainsmart.mailing.infrastructure.config.MailingProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lukawska.trainsmart.mailing.testutil.MailingTestData.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(PostgresTestConfig.class)
class MailServiceIT {

    @MockitoBean
    private MailSender mailSender;

    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private MailingProperties mailingProperties;

    @Autowired
    private MailService mailService;

    @Test
    void shouldSendAndSaveMailSuccess() throws MessagingException {
        //given
        final MailDetails mailDetails = mailDetailsWithAttachments();
        doNothing().when(mailSender).sendEmail(mailDetails);

        //when
        MailResponse mailResponse = mailService.sendMail(mailDetails);

        //then
        verify(mailSender, times(1)).sendEmail(mailDetails);
        MailEntity savedMail = mailRepository.findById(mailResponse.id())
                                             .orElseThrow(() -> new AssertionError("Mail not saved"));

        assertThat(mailResponse.id()).isEqualTo(savedMail.getId());
        assertThat(mailResponse.recipients()).isEqualTo(savedMail.getRecipients());
        assertThat(mailResponse.subject()).isEqualTo(savedMail.getSubject());
        assertThat(mailResponse.from()).isEqualTo(mailingProperties.getFrom());
        assertThat(mailResponse.replyTo()).isEqualTo(mailingProperties.getReplyTo());
    }

    @Test
    void shouldReturnMailById() {
        //given
        final MailEntity mailEntity = mailEntityWithAttachments();
        final List<AttachmentMeta> expectedAttachmentMeta = mapToAttachmentMetaList(mailEntity.getAttachments());
        mailRepository.save(mailEntity);

        //when
        MailResponse result = mailService.getMailResponseById(mailEntity.getId());

        //then
        assertThat(result.id()).isEqualTo(mailEntity.getId());
        assertThat(result.recipients()).isEqualTo(mailEntity.getRecipients());
        assertThat(result.attachments()).isEqualTo(expectedAttachmentMeta);
    }

    @Test
    void shouldReturnAllMailsByRecipient() {
        //given
        final String recipient = randomEmail();
        mailRepository.saveAll(List.of(mailWithRecipient(recipient), mailWithRecipient(randomEmail())));

        //when
        List<MailResponse> mailResponses = mailService.getAllMailsByRecipient(recipient);

        //then
        List<MailEntity> mailsByRecipient = mailRepository.findAllByRecipient(recipient);
        assertThat(mailResponses.size()).isEqualTo(mailsByRecipient.size());
        assertThat(mailResponses).extracting(MailResponse::recipients)
                                 .allMatch(response -> response.contains(recipient));
    }

    @Test
    void shouldReturnAllMailsBySubjectContaining() {
        //given
        final String keyword = UUID.randomUUID().toString();
        mailRepository.saveAll(List.of(mailWithSubject(keyword + "subject"), mailWithSubject("different subject")));

        //when
        List<MailResponse> mailResponses = mailService.getAllMailsBySubjectContaining(keyword);

        //then
        List<MailEntity> mailsWithKeyword = mailRepository.findAllBySubjectContaining(keyword);
        assertThat(mailResponses.size()).isEqualTo(mailsWithKeyword.size());
        assertThat(mailResponses).extracting(MailResponse::subject).allMatch(r -> r.contains(keyword));
    }

    @Test
    void shouldThrowExceptionWhenSendMailError() throws MessagingException {
        //given
        final MailDetails mailDetails = mailDetailsWithAttachments();
        doThrow(new MessagingException("send error")).when(mailSender).sendEmail(mailDetails);

        //when && then
        assertThatThrownBy(() -> mailService.sendMail(mailDetails))
                .isInstanceOf(MailingException.class)
                .hasMessage(ExceptionType.MAIL_SEND_ERROR.getMessage());

        assertThat(mailRepository.count()).isZero();
    }

    @Test
    void shouldThrowInvalidAttachmentExtensionWhenSendMail() {
        //given
        final MailDetails mailDetails = mailDetailsWithInvalidAttachment();

        //when && then
        assertThatThrownBy(() -> mailService.sendMail(mailDetails))
                .isInstanceOf(MailingException.class)
                .hasMessage(ExceptionType.INVALID_ATTACHMENT_EXTENSION.getMessage());

        assertThat(mailRepository.count()).isZero();
    }

    private List<AttachmentMeta> mapToAttachmentMetaList(List<Attachment> attachments) {
        return attachments.stream()
                          .map(att -> new AttachmentMeta(att.getFileName(), att.getSize()))
                          .toList();
    }
}
