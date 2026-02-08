package org.lukawska.trainsmart.mailing.application.service;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.mailing.application.dto.MailDetails;
import org.lukawska.trainsmart.mailing.application.exception.ExceptionType;
import org.lukawska.trainsmart.mailing.application.exception.MailingException;
import org.lukawska.trainsmart.mailing.domain.entities.MailEntity;
import org.lukawska.trainsmart.mailing.domain.repositories.MailRepository;
import org.lukawska.trainsmart.mailing.domain.valueObjects.Attachment;
import org.lukawska.trainsmart.mailing.infrastructure.config.MailingProperties;
import org.lukawska.trainsmart.mailing.model.AttachmentMeta;
import org.lukawska.trainsmart.mailing.model.MailResponse;
import org.lukawska.trainsmart.testutils.TestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import({PostgresTestConfig.class, TestFixtures.class})
class MailServiceIT {

    @MockitoBean
    private MailSender mailSender;

    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private MailingProperties mailingProperties;

    @Autowired
    private MailService mailService;

    @Autowired
    private TestFixtures testFixtures;

    @Test
    void shouldSendAndSaveMailSuccess() throws MessagingException {
        //given
        final MailDetails mailDetails = TestData.mailDetailsWithAttachments();
        doNothing().when(mailSender).sendEmail(mailDetails);

        //when
        MailResponse mailResponse = mailService.sendMail(mailDetails);

        //then
        verify(mailSender, times(1)).sendEmail(mailDetails);
        MailEntity savedMail = mailRepository.findById(mailResponse.getId())
                                             .orElseThrow(() -> new AssertionError("Mail not saved"));

        assertThat(mailResponse.getId()).isEqualTo(savedMail.getId());
        assertThat(mailResponse.getRecipients()).isEqualTo(savedMail.getRecipients());
        assertThat(mailResponse.getSubject()).isEqualTo(savedMail.getSubject());
        assertThat(mailResponse.getFrom()).isEqualTo(mailingProperties.getFrom());
        assertThat(mailResponse.getReplyTo()).isEqualTo(mailingProperties.getReplyTo());
    }

    @Test
    void shouldReturnMailById() {
        //given
        final MailEntity mailEntity = testFixtures.mail().save();
        final List<AttachmentMeta> expectedAttachmentMeta = mapToAttachmentMetaList(mailEntity.getAttachments());

        //when
        MailResponse result = mailService.getMailResponseById(mailEntity.getId());

        //then
        assertThat(result.getId()).isEqualTo(mailEntity.getId());
        assertThat(result.getRecipients()).isEqualTo(mailEntity.getRecipients());
        assertThat(result.getAttachments()).isEqualTo(expectedAttachmentMeta);
    }

    @Test
    void shouldReturnAllMailsByRecipient() {
        //given
        final MailEntity mail1 = testFixtures.mail().save();
        final String recipient = TestData.email();
        final MailEntity mail2 = testFixtures.mail()
                                             .withRecipients(List.of(recipient))
                                             .save();

        //when
        List<MailResponse> mailResponses = mailService.getAllMailsByRecipient(recipient);

        //then
        List<MailEntity> mailsByRecipient = mailRepository.findAllByRecipient(recipient);
        assertThat(mailResponses.size()).isEqualTo(mailsByRecipient.size());
        assertThat(mailResponses.getFirst().getRecipients()).isEqualTo(mail2.getRecipients());
        assertThat(mailResponses.getFirst().getRecipients()).isNotEqualTo(mail1.getRecipients());
    }

    @Test
    void shouldReturnAllMailsBySubjectContaining() {
        //given
        final MailEntity mail1 = testFixtures.mail().save();
        final String keyword = TestData.text();
        final MailEntity mail2 = testFixtures.mail()
                                             .withSubject(keyword + "text")
                                             .save();

        //when
        List<MailResponse> mailResponses = mailService.getAllMailsBySubjectContaining(keyword);

        //then
        List<MailEntity> mailsWithKeyword = mailRepository.findAllBySubjectContaining(keyword);
        assertThat(mailResponses.size()).isEqualTo(mailsWithKeyword.size());
        assertThat(mailResponses.getFirst().getSubject()).isEqualTo(mail2.getSubject());
        assertThat(mailResponses).extracting(MailResponse::getSubject).doesNotContain(mail1.getSubject());
    }

    @Test
    void shouldThrowExceptionWhenSendMailError() throws MessagingException {
        //given
        final MailDetails mailDetails = TestData.mailDetailsWithAttachments();
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
        final MailDetails mailDetails = MailDetails.builder()
                                                   .recipients(List.of(TestData.email()))
                                                   .text(TestData.text())
                                                   .subject(TestData.text())
                                                   .isHtml(false)
                                                   .attachments(List.of(new Attachment("file.txt", new byte[]{1, 2})))
                                                   .build();

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
