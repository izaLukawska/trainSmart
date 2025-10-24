package org.lukawska.trainSmart.mailing.application.service;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainSmart.mailing.application.dto.MailRequest;
import org.lukawska.trainSmart.mailing.application.dto.MailResponse;
import org.lukawska.trainSmart.mailing.application.exception.ExceptionType;
import org.lukawska.trainSmart.mailing.application.exception.MailingException;
import org.lukawska.trainSmart.mailing.domain.entities.MailEntity;
import org.lukawska.trainSmart.mailing.domain.repository.MailRepository;
import org.lukawska.trainSmart.mailing.infrastructure.config.MailingProperties;
import org.lukawska.trainSmart.mailing.infrastructure.external.AttachmentValidatorAdapter;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lukawska.trainSmart.mailing.testdata.MailingTestData.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private MailRepository mailRepository;

    @Mock
    private MailSender mailSender;

    @Mock
    private AttachmentValidatorAdapter attachmentValidatorAdapter;

    @Mock
    private MailingProperties mailingProperties;

    @InjectMocks
    private MailService mailService;

    @Test
    void shouldSendMailWithAttachmentSuccess() throws MessagingException {
        //given
        final MailRequest request = randomMailRequest();
        doNothing().when(attachmentValidatorAdapter).validateAttachments(request.attachments());

        //when
        mailService.sendMail(request);

        //then
        verify(mailingProperties).getFrom();
        verify(mailingProperties).getReplyTo();
        verify(attachmentValidatorAdapter).validateAttachments(request.attachments());
        verify(mailSender).sendEmail(eq(request));
    }

    @Test
    void shouldReturnAllMailsContainingKeyword() {
        //given
        final String keyword = randomText();
        final List<MailEntity> randomList = List.of(randomMailEntity());
        when(mailRepository.findAllBySubjectContaining(keyword)).thenReturn(randomList);
        final String expectedSubject = randomList.getFirst().getSubject();

        //when
        List<MailResponse> result = mailService.getAllMailsBySubjectContaining(keyword);

        //then
        assertThat(result).extracting(MailResponse::subject)
                          .containsExactly(expectedSubject);
    }

    @Test
    void shouldReturnAllMailsByRecipient() {
        //given
        final String recipientMail = randomMail();
        final MailEntity randomMail = randomMailEntity();
        when(mailRepository.findAllByRecipient(recipientMail)).thenReturn(List.of(randomMail));

        //when
        List<MailResponse> result = mailService.getAllMailsByRecipient(recipientMail);

        //then
        assertThat(result).extracting(MailResponse::recipients)
                          .containsExactly(randomMail.getRecipients());

    }

    @Test
    void shouldReturnAllMails() {
        //given
        final List<MailEntity> randomList = List.of(randomMailEntity());
        when(mailRepository.findAll()).thenReturn(randomList);
        final List<String> expectedRecipients = randomList.getFirst().getRecipients();

        //when
        List<MailResponse> result = mailService.getAllMails();

        //then
        assertThat(result).extracting(MailResponse::recipients)
                          .containsExactly(expectedRecipients);
    }

    @Test
    void shouldReturnMailWhenGetMailByIdFound() {
        //given
        final Long id = new Random().nextLong();
        final MailEntity randomMail = randomMailEntity();
        when(mailRepository.findById(id)).thenReturn(Optional.of(randomMail));

        //when
        MailResponse result = mailService.getMailResponseById(id);

        //then
        assertThat(result).extracting(MailResponse::recipients, MailResponse::subject)
                          .contains(randomMail.getRecipients(), randomMail.getSubject());
    }

    @Test
    void shouldThrowMessagingExceptionWhenSendMail() throws MessagingException {
        //given
        final MailRequest mailRequest = randomMailRequest();
        doThrow(new MessagingException(randomText()))
                .when(mailSender).sendEmail(eq(mailRequest));

        //when && then
        assertThatThrownBy(() -> mailService.sendMail(mailRequest))
                .isInstanceOf(MailingException.class)
                .hasMessage(ExceptionType.MAIL_SEND_ERROR.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenGetMailByIdNotFound() {
        //given
        final Long id = new Random().nextLong();
        when(mailRepository.findById(id)).thenReturn(Optional.empty());

        //when && then
        assertThatThrownBy(() -> mailService.getMailResponseById(id))
                .isInstanceOf(MailingException.class)
                .hasMessage(ExceptionType.MAIL_NOT_FOUND.getMessage());
    }
}
