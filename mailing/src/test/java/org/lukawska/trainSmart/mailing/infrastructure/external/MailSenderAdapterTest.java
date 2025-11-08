package org.lukawska.trainSmart.mailing.infrastructure.external;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainSmart.mailing.infrastructure.config.MailingProperties;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.lukawska.trainSmart.mailing.testdata.MailingTestData.randomMail;
import static org.lukawska.trainSmart.mailing.testdata.MailingTestData.randomMailRequest;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailSenderAdapterTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MailingProperties mailingProperties;

    @InjectMocks
    private MailSenderAdapter mailSenderAdapter;

    @Test
    void shouldSendMailWithAttachmentsSuccess() throws MessagingException {
        //given
        final MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(mailingProperties.getFrom()).thenReturn(randomMail());
        when(mailingProperties.getReplyTo()).thenReturn(randomMail());

        //when
        mailSenderAdapter.sendEmail(randomMailRequest(true));

        //then
        verify(mailSender).send(mimeMessage);
    }
}
