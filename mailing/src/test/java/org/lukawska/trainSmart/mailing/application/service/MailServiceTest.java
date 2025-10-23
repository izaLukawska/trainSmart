package org.lukawska.trainSmart.mailing.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainSmart.mailing.application.dto.MailResponse;
import org.lukawska.trainSmart.mailing.application.exception.ExceptionType;
import org.lukawska.trainSmart.mailing.application.exception.MailingException;
import org.lukawska.trainSmart.mailing.domain.entities.MailEntity;
import org.lukawska.trainSmart.mailing.domain.repository.MailRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private MailRepository mailRepository;

    @Mock
    private MailSender mailSender;

    @InjectMocks
    private MailService mailService;

    private MailEntity mail;

    @BeforeEach
    void setUp() {
        mail = MailEntity.builder()
                         .recipients(List.of(UUID.randomUUID().toString()))
                         .subject(UUID.randomUUID().toString())
                         .build();
    }

    @Test
    void shouldReturnAllMailsContainingKeyword() {
        //given
        final String keyword = UUID.randomUUID().toString();
        when(mailRepository.findAllBySubjectContaining(keyword)).thenReturn(List.of(mail));

        //when
        List<MailResponse> result = mailService.getAllMailsBySubjectContaining(keyword);

        //then
        assertThat(result).singleElement().extracting(MailResponse::recipients, MailResponse::subject)
                          .contains(mail.getRecipients(), mail.getSubject());
    }

    @Test
    void shouldReturnAllMailsByRecipient() {
        //given
        final String recipient = mail.getRecipients().getFirst();
        when(mailRepository.findAllByRecipient(recipient)).thenReturn(List.of(mail));

        //when
        List<MailResponse> result = mailService.getAllMailsByRecipient(recipient);

        //then
        assertThat(result).singleElement().extracting(MailResponse::recipients, MailResponse::subject)
                          .contains(mail.getRecipients(), mail.getSubject());
    }

    @Test
    void shouldReturnAllMails() {
        //given
        when(mailRepository.findAll()).thenReturn(List.of(mail));

        //when
        List<MailResponse> result = mailService.getAllMails();

        //then
        assertThat(result).singleElement().extracting(MailResponse::recipients, MailResponse::subject)
                          .contains(mail.getRecipients(), mail.getSubject());
    }

    @Test
    void shouldReturnMailWhenGetMailByIdFound() {
        //given
        final Long id = new Random().nextLong();
        when(mailRepository.findById(id)).thenReturn(Optional.of(mail));

        //when
        MailResponse result = mailService.getMailResponseById(id);

        //then
        assertThat(result).extracting(MailResponse::recipients, MailResponse::subject)
                          .contains(mail.getRecipients(), mail.getSubject());
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
