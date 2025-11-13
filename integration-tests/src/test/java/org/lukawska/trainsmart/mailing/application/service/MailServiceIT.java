package org.lukawska.trainsmart.mailing.application.service;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestBase;
import org.lukawska.trainsmart.mailing.application.dto.MailRequest;
import org.lukawska.trainsmart.mailing.application.dto.MailResponse;
import org.lukawska.trainsmart.mailing.application.exception.ExceptionType;
import org.lukawska.trainsmart.mailing.application.exception.MailingException;
import org.lukawska.trainsmart.mailing.domain.entities.MailEntity;
import org.lukawska.trainsmart.mailing.domain.repository.MailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lukawska.trainsmart.mailing.testdata.MailingTestData.*;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
@Transactional
public class MailServiceIT extends PostgresTestBase {

    @MockitoBean
    private MailSender mailSender;

    @Autowired
    private MailRepository mailRepository;

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
        MailEntity savedMail = mailRepository.findById(mailResponse.id())
                                             .orElseThrow(() -> new AssertionError("Mail not saved"));

        assertThat(mailResponse.id()).isEqualTo(savedMail.getId());
        assertThat(mailResponse.recipients()).isEqualTo(savedMail.getRecipients());
        assertThat(mailResponse.from()).isEqualTo("test-from@example.com");
        assertThat(mailResponse.replyTo()).isEqualTo("test-replyto@example.com");
        assertThat(mailResponse.subject()).isEqualTo(savedMail.getSubject());
    }

    @Test
    void shouldReturnMailById() {
        //given
        final MailEntity mailEntity = mailEntityWithAttachments();
        mailRepository.save(mailEntity);

        //when
        MailResponse result = mailService.getMailResponseById(mailEntity.getId());

        //then
        assertThat(result.id()).isEqualTo(mailEntity.getId());
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
                                 .allMatch(response -> response.contains("test@test.com"));
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
    void shouldThrowInvalidAttachmentExtensionWhenSendMail() {
        //given
        final MailRequest mailRequest = mailRequestWithInvalidAttachment();

        //when && then
        assertThatThrownBy(() -> mailService.sendMail(mailRequest))
                .isInstanceOf(MailingException.class)
                .hasMessage(ExceptionType.INVALID_ATTACHMENT_EXTENSION.getMessage());
    }
}
