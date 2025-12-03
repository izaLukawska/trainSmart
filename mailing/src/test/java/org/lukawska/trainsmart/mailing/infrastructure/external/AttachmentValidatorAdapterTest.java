package org.lukawska.trainsmart.mailing.infrastructure.external;

import org.apache.tika.Tika;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.mailing.application.exception.ExceptionType;
import org.lukawska.trainsmart.mailing.application.exception.MailingException;
import org.lukawska.trainsmart.mailing.domain.valueObject.Attachment;
import org.lukawska.trainsmart.mailing.infrastructure.config.MailingProperties;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lukawska.trainsmart.mailing.testdata.MailingTestData.randomText;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttachmentValidatorAdapterTest {

    @Mock
    private MailingProperties mailingProperties;

    @Mock
    private Tika tika;

    @InjectMocks
    private AttachmentValidatorAdapter attachmentValidatorAdapter;

    @Test
    void shouldValidateAttachmentSuccess() {
        //given
        final Attachment attachment = new Attachment(randomText() + ".pdf", new byte[20]);
        when(mailingProperties.getValidMimeTypes()).thenReturn(Map.of("pdf", List.of("application/pdf")));
        when(mailingProperties.getMaxSizeBytes()).thenReturn(30L);
        when(tika.detect(attachment.getContent(), attachment.getFileName())).thenReturn("application/pdf");

        // when && then
        assertThatCode(() -> attachmentValidatorAdapter.validateAttachments(List.of(attachment)))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowExceptionWhenInvalidAttachmentExtension() {
        //given
        final Attachment attachment = new Attachment(randomText(), new byte[20]);

        //when && then
        assertThatThrownBy(() -> attachmentValidatorAdapter.validateAttachments(List.of(attachment)))
                .isInstanceOf(MailingException.class)
                .hasMessage(ExceptionType.INVALID_ATTACHMENT_EXTENSION.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAttachmentTooLarge() {
        //given
        final Attachment attachment = new Attachment(randomText() + ".pdf", new byte[20]);
        when(mailingProperties.getValidMimeTypes()).thenReturn(Map.of("pdf", List.of(randomText())));
        when(mailingProperties.getMaxSizeBytes()).thenReturn(1L);

        //when && then
        assertThatThrownBy(() -> attachmentValidatorAdapter.validateAttachments(List.of(attachment)))
                .isInstanceOf(MailingException.class)
                .hasMessage(ExceptionType.ATTACHMENT_TOO_LARGE.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenInvalidMimeType() {
        //given
        final Attachment attachment = new Attachment(randomText() + ".pdf", new byte[20]);
        when(mailingProperties.getValidMimeTypes()).thenReturn(Map.of("pdf", List.of("application/pdf")));
        when(mailingProperties.getMaxSizeBytes()).thenReturn(30L);
        when(tika.detect(attachment.getContent(), attachment.getFileName())).thenReturn(randomText());

        //when && then
        assertThatThrownBy(() -> attachmentValidatorAdapter.validateAttachments(List.of(attachment)))
                .isInstanceOf(MailingException.class)
                .hasMessage(ExceptionType.INVALID_ATTACHMENT_TYPE.getMessage());

    }
}
