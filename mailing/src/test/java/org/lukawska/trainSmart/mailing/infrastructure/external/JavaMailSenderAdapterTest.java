package org.lukawska.trainSmart.mailing.infrastructure.external;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.lukawska.trainSmart.mailing.application.dto.Attachment;
import org.lukawska.trainSmart.mailing.application.dto.MailRequest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JavaMailSenderAdapterTest {

	@Mock
	private JavaMailSender mailSender;

	@InjectMocks
	private JavaMailSenderAdapter mailSenderAdapter;

	@Test
	void shouldSendEmail_whenMailRequestIsValid() throws MessagingException {
		//given
		final Attachment attachment = mock(Attachment.class);
		when(attachment.fileName()).thenReturn("file.txt");
		when(attachment.mimeType()).thenReturn("text/plain");
		when(attachment.source()).thenReturn(new ByteArrayResource("Test source".getBytes()));

		final MailRequest mailRequest = MailRequest.builder()
		                                           .recipients(new String[]{"test@example.com"})
		                                           .subject("Test Subject")
		                                           .text("Test Body")
		                                           .isHtml(true)
		                                           .attachments(List.of(attachment))
		                                           .build();

		MimeMessage mimeMessage = mock(MimeMessage.class);
		when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
		//when
		mailSenderAdapter.sendEmail(mailRequest);

		//then
		verify(mailSender, times(1)).send(mimeMessage);
	}

	@Test
	void shouldThrowIllegalArgumentException_whenMailRequestIsNull() {
		//when && then
		assertThrows(IllegalArgumentException.class, () -> mailSenderAdapter.sendEmail(null));
	}

	@ParameterizedTest
	@NullAndEmptySource
	void shouldThrowIllegalArgumentException_whenRecipientIsInvalid(String[] recipient) {
		MailRequest mailRequest = MailRequest.builder().recipients(recipient).build();
		assertThrows(IllegalArgumentException.class, () -> mailSenderAdapter.sendEmail(mailRequest));
	}
}