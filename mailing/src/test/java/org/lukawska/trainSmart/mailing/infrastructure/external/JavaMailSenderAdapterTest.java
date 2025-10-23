package org.lukawska.trainSmart.mailing.infrastructure.external;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainSmart.mailing.application.dto.MailRequest;
import org.lukawska.trainSmart.mailing.domain.valueObject.Attachment;
import org.lukawska.trainSmart.mailing.domain.valueObject.AttachmentType;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JavaMailSenderAdapterTest {

    @Mock
    private JavaMailSender mailSender;

    private JavaMailSenderAdapter adapter;

    @BeforeEach
    void setUp() {
        String TEST_MAIL_FROM = "sender@test.com";
        String TEST_REPLY_TO = "reply@test.com";
        adapter = new JavaMailSenderAdapter(mailSender, TEST_MAIL_FROM, TEST_REPLY_TO);
    }

    @Test
    void shouldSendMailWithoutAttachmentsSuccess() throws MessagingException {
        //given
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        Attachment attachment = new Attachment(UUID.randomUUID().toString().concat(".pdf"),
                                               AttachmentType.PDF,
                                               new ByteArrayResource(new byte[10]));
        MailRequest request = new MailRequest(List.of(UUID.randomUUID().toString()),
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
