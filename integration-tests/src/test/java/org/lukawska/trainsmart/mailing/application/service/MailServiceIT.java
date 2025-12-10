package org.lukawska.trainsmart.mailing.application.service;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestBase;
import org.lukawska.trainsmart.mailing.application.dto.AttachmentMeta;
import org.lukawska.trainsmart.mailing.application.dto.MailRequest;
import org.lukawska.trainsmart.mailing.application.dto.MailResponse;
import org.lukawska.trainsmart.mailing.application.exception.ExceptionType;
import org.lukawska.trainsmart.mailing.application.exception.MailingException;
import org.lukawska.trainsmart.mailing.domain.entities.MailEntity;
import org.lukawska.trainsmart.mailing.domain.repositories.MailRepository;
import org.lukawska.trainsmart.mailing.domain.valueObjects.Attachment;
import org.lukawska.trainsmart.mailing.infrastructure.config.MailingProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lukawska.trainsmart.mailing.testutil.MailingTestData.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class MailServiceIT extends PostgresTestBase {

    @MockitoBean
    private MailSender mailSender;

    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private MailingProperties mailingProperties;

    @Autowired
    private MailService mailService;

    @AfterEach
    void cleanUp() {
        mailRepository.deleteAll();
    }

    @Test
    void shouldSendAndSaveMailSuccess() throws MessagingException {
        //given
        final MailRequest mailRequest = mailRequestWithAttachments();
        doNothing().when(mailSender).sendEmail(mailRequest);

        //when
        MailResponse mailResponse = mailService.sendMail(mailRequest);

        //then
        verify(mailSender, times(1)).sendEmail(mailRequest);
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
        final String recipient = "test@test.com";
        mailRepository.saveAll(List.of(mailWithRecipient(recipient), mailWithRecipient("test1@test.com")));

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
        final String keyword = "test";
        mailRepository.saveAll(List.of(mailWithSubject("test subject"), mailWithSubject("different subject")));

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
        final MailRequest mailRequest = mailRequestWithAttachments();
        doThrow(new MessagingException("send error")).when(mailSender).sendEmail(mailRequest);

        //when && then
        assertThatThrownBy(() -> mailService.sendMail(mailRequest))
                .isInstanceOf(MailingException.class)
                .hasMessage(ExceptionType.MAIL_SEND_ERROR.getMessage());

        assertThat(mailRepository.count()).isZero();
    }

    @Test
    void shouldThrowInvalidAttachmentExtensionWhenSendMail() {
        //given
        final MailRequest mailRequest = mailRequestWithInvalidAttachment();

        //when && then
        assertThatThrownBy(() -> mailService.sendMail(mailRequest))
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
