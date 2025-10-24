package org.lukawska.trainSmart.mailing.infrastructure.external;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainSmart.mailing.application.dto.MailRequest;
import org.lukawska.trainSmart.mailing.domain.valueObject.Attachment;
import org.lukawska.trainSmart.mailing.infrastructure.config.MailingProperties;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JavaMailSenderAdapterTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MailingProperties properties;

    @InjectMocks
    private JavaMailSenderAdapter adapter;

    @Test
    void shouldSendMailWithAttachmentsSuccess() throws MessagingException {
        //given
        final String randomMail = UUID.randomUUID() + "@test.com";
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(properties.getFrom()).thenReturn(randomMail);
        when(properties.getReplyTo()).thenReturn(randomMail);

        Attachment attachment = new Attachment(UUID.randomUUID().toString().concat(".pdf"),
                                               new byte[10]);
        MailRequest request = new MailRequest(List.of(randomMail),
                                              List.of(),
                                              List.of(),
                                              UUID.randomUUID().toString(),
                                              UUID.randomUUID().toString(),
                                              new Random().nextBoolean(),
                                              List.of(attachment));

        //when
        adapter.sendEmail(request, UUID.randomUUID().toString());

        //then
        verify(mailSender).send(mimeMessage);
    }
}
