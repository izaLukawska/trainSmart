package org.lukawska.trainSmart.mailing.presentation.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainSmart.mailing.application.dto.MailRequest;
import org.lukawska.trainSmart.mailing.application.dto.MailResponse;
import org.lukawska.trainSmart.mailing.application.service.MailService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailControllerTest {

    @Mock
    private MailService mailService;

    @InjectMocks
    private MailController mailController;

    private MailResponse response;

    @BeforeEach
    void setUp() {
        response = mock(MailResponse.class);
    }

    @Test
    void shouldReturnMailResponseSent() {
        // given
        MailRequest request = mock(MailRequest.class);
        when(mailService.sendMail(any(MailRequest.class))).thenReturn(response);

        // when
        MailResponse result = mailController.sendMail(request);

        // then
        assertSame(response, result);
        verify(mailService, times(1)).sendMail(request);
    }

    @Test
    void shouldReturnMailResponseById() {
        // given
        Long id = 42L;
        when(mailService.getMailResponseById(id)).thenReturn(response);

        // when
        MailResponse result = mailController.getMailById(id);

        // then
        assertSame(response, result);
        verify(mailService, times(1)).getMailResponseById(id);
    }

    @Test
    void shouldReturnAllMails() {
        // given
        List<MailResponse> expected = List.of(response);
        when(mailService.getAllMails()).thenReturn(expected);

        // when
        List<MailResponse> result = mailController.getAllMails();

        // then
        assertSame(expected, result);
        verify(mailService, times(1)).getAllMails();
    }

    @Test
    void shouldReturnAllMailsByRecipient() {
        // given
        String recipient = UUID.randomUUID().toString().concat("@test.com");
        List<MailResponse> expected = List.of(response);
        when(mailService.getAllMailsByRecipient(recipient)).thenReturn(expected);

        // when
        List<MailResponse> result = mailController.getAllMailsByRecipient(recipient);

        // then
        assertSame(expected, result);
        verify(mailService, times(1)).getAllMailsByRecipient(recipient);
    }

    @Test
    void shouldReturnAllMailsBySubjectContaining() {
        // given
        String keyword = UUID.randomUUID().toString();
        List<MailResponse> expected = List.of(response);
        when(mailService.getAllMailsBySubjectContaining(keyword)).thenReturn(expected);

        // when
        List<MailResponse> result = mailController.getAllMailsBySubjectContaining(keyword);

        // then
        assertSame(expected, result);
        verify(mailService, times(1)).getAllMailsBySubjectContaining(keyword);
    }
}
