package org.lukawska.trainSmart.mailing.presentation.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainSmart.mailing.application.dto.MailRequest;
import org.lukawska.trainSmart.mailing.application.dto.MailResponse;
import org.lukawska.trainSmart.mailing.application.service.MailService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lukawska.trainSmart.mailing.testdata.MailingTestData.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailControllerTest {

    @Mock
    private MailService mailService;

    @InjectMocks
    private MailController mailController;

    @Test
    void shouldReturnMailResponseSent() {
        // given
        final MailRequest request = randomMailRequest(true);
        final MailResponse expectedResponse = randomMailResponse();
        when(mailService.sendMail(any(MailRequest.class))).thenReturn(expectedResponse);

        MockHttpServletRequest servletRequest = new MockHttpServletRequest("POST", "/api/mail/send");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(servletRequest));

        // when
        ResponseEntity<MailResponse> result = mailController.sendMail(request);

        // then
        assertThat(result.getBody()).isEqualTo(expectedResponse);
        assertThat(result.getStatusCode().value()).isEqualTo(201);
        verify(mailService, times(1)).sendMail(request);
    }

    @Test
    void shouldReturnMailResponseById() {
        // given
        final Long id = new Random().nextLong();
        final MailResponse expectedResponse = randomMailResponse();
        when(mailService.getMailResponseById(id)).thenReturn(expectedResponse);

        // when
        MailResponse result = mailController.getMailById(id);

        // then
        assertThat(result).isEqualTo(expectedResponse);
        verify(mailService, times(1)).getMailResponseById(id);
    }

    @Test
    void shouldReturnAllMails() {
        // given
        final List<MailResponse> expectedResponse = List.of(randomMailResponse());
        when(mailService.getAllMails()).thenReturn(expectedResponse);

        // when
        List<MailResponse> result = mailController.getAllMails();

        // then
        assertThat(result).isEqualTo(expectedResponse);
        verify(mailService, times(1)).getAllMails();
    }

    @Test
    void shouldReturnAllMailsByRecipient() {
        // given
        final String recipient = randomMail();
        final List<MailResponse> expectedResponse = List.of(randomMailResponse());
        when(mailService.getAllMailsByRecipient(recipient)).thenReturn(expectedResponse);

        // when
        List<MailResponse> result = mailController.getAllMailsByRecipient(recipient);

        // then
        assertThat(result).isEqualTo(expectedResponse);
        verify(mailService, times(1)).getAllMailsByRecipient(recipient);
    }

    @Test
    void shouldReturnAllMailsBySubjectContaining() {
        // given
        final String keyword = randomText();
        final List<MailResponse> expectedResponse = List.of(randomMailResponse());
        when(mailService.getAllMailsBySubjectContaining(keyword)).thenReturn(expectedResponse);

        // when
        List<MailResponse> result = mailController.getAllMailsBySubjectContaining(keyword);

        // then
        assertThat(result).isEqualTo(expectedResponse);
        verify(mailService, times(1)).getAllMailsBySubjectContaining(keyword);
    }
}
