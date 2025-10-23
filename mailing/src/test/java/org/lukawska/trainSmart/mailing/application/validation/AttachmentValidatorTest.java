package org.lukawska.trainSmart.mailing.application.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukawska.trainSmart.mailing.application.exception.ExceptionType;
import org.lukawska.trainSmart.mailing.domain.valueObject.Attachment;
import org.lukawska.trainSmart.mailing.domain.valueObject.AttachmentType;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;

class AttachmentValidatorTest {

    private AttachmentValidator attachmentValidator;

    @BeforeEach
    void setUp() {
        long maxSize = 5 * 1024 * 1024;
        this.attachmentValidator = new AttachmentValidator(maxSize);
    }

    @Test
    void shouldValidateAttachmentsSuccess() {
        //given
        String fileName = UUID.randomUUID() + "." + AttachmentType.PDF.getExtension();
        Attachment attachment = new Attachment(fileName, AttachmentType.PDF, new ByteArrayResource(new byte[10]));
        List<Attachment> attachments = List.of(attachment);

        //when && then
        assertThatCode(() -> attachmentValidator.validateAttachments(attachments)).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowExceptionWhenAttachmentNameInvalid() {
        //given
        Attachment attachment = new Attachment("", AttachmentType.PDF, new ByteArrayResource(new byte[10]));
        List<Attachment> attachments = List.of(attachment);

        //when && then
        assertThatCode(() -> attachmentValidator.validateAttachments(attachments))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(ExceptionType.INVALID_ATTACHMENT_NAME.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAttachmentExtensionInvalid() {
        //given
        String fileName = UUID.randomUUID() + ".invalid";
        Attachment attachment = new Attachment(fileName, AttachmentType.EXCEL, new ByteArrayResource(new byte[10]));
        List<Attachment> attachments = List.of(attachment);

        //when && then
        assertThatCode(() -> attachmentValidator.validateAttachments(attachments))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(ExceptionType.INVALID_ATTACHMENT_EXTENSION.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAttachmentTypeMissing() {
        //given
        String fileName = UUID.randomUUID() + ".pdf";
        Attachment attachment = new Attachment(fileName, null, new ByteArrayResource(new byte[10]));
        List<Attachment> attachments = List.of(attachment);

        //when && then
        assertThatCode(() -> attachmentValidator.validateAttachments(attachments))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(ExceptionType.MISSING_ATTACHMENT.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAttachmentSizeToLarge() {
        //given
        String fileName = UUID.randomUUID() + "." + AttachmentType.PDF.getExtension();
        Attachment attachment = new Attachment(fileName,
                                               AttachmentType.PDF,
                                               new ByteArrayResource(new byte[100 * 1024 * 1024]));
        List<Attachment> attachments = List.of(attachment);

        //when && then
        assertThatCode(() -> attachmentValidator.validateAttachments(attachments))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(ExceptionType.ATTACHMENT_TOO_LARGE.getMessage());
    }
}
