package org.lukawska.trainsmart.mailing.application.service;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.mailing.application.dto.MailDetails;
import org.lukawska.trainsmart.mailing.application.exception.ExceptionType;
import org.lukawska.trainsmart.mailing.application.exception.MailingException;
import org.lukawska.trainsmart.mailing.domain.entities.MailEntity;
import org.lukawska.trainsmart.mailing.domain.repositories.MailRepository;
import org.lukawska.trainsmart.mailing.infrastructure.config.MailingProperties;
import org.lukawska.trainsmart.mailing.infrastructure.external.AttachmentValidatorAdapter;
import org.lukawska.trainsmart.mailing.model.MailResponse;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lukawska.trainsmart.mailing.testdata.MailingTestData.*;
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
        final MailDetails mailRequest = mailDetails(true);

        //when
        MailResponse result = mailService.sendMail(mailRequest);

        //then
        assertThat(result.getRecipients()).isEqualTo(mailRequest.recipients());
        assertThat(result.getAttachments().size()).isEqualTo(mailRequest.attachments().size());
        verify(mailingProperties).getFrom();
        verify(mailingProperties).getReplyTo();
        verify(attachmentValidatorAdapter).validateAttachments(mailRequest.attachments());
        verify(mailSender).sendEmail(eq(mailRequest));
    }

    @Test
    void shouldSendMailWithoutAttachmentSuccess() throws MessagingException {
        //given
        final MailDetails mailRequest = mailDetails(false);

        //when
        MailResponse result = mailService.sendMail(mailRequest);

        //then
        assertThat(result.getRecipients()).isEqualTo(mailRequest.recipients());
        assertThat(result.getAttachments()).isEmpty();
        verify(mailingProperties).getFrom();
        verify(mailingProperties).getReplyTo();
        verify(attachmentValidatorAdapter, never()).validateAttachments(mailRequest.attachments());
        verify(mailSender).sendEmail(eq(mailRequest));
    }

    @Test
    void shouldReturnAllMailsContainingKeyword() {
        //given
        final String keyword = randomText();
        final List<MailEntity> randomMailList = List.of(randomMailEntity());
        when(mailRepository.findAllBySubjectContaining(keyword)).thenReturn(randomMailList);
        final String expectedSubject = randomMailList.getFirst().getSubject();

        //when
        List<MailResponse> result = mailService.getAllMailsBySubjectContaining(keyword);

        //then
        assertThat(result).extracting(MailResponse::getSubject).containsExactly(expectedSubject);
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
        assertThat(result).extracting(MailResponse::getRecipients).containsExactly(randomMail.getRecipients());
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
        assertThat(result.getRecipients()).contains(randomMail.getRecipients().toArray(String[]::new));
        assertThat(result.getSubject()).isEqualTo(randomMail.getSubject());
    }

    @Test
    void shouldThrowMessagingExceptionWhenSendMail() throws MessagingException {
        //given
        final MailDetails mailRequest = mailDetails(new Random().nextBoolean());
        doThrow(new MessagingException(randomText())).when(mailSender).sendEmail(mailRequest);

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
