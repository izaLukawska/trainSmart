package org.lukawska.trainSmart.mailing.application.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukawska.trainSmart.mailing.application.exception.ExceptionType;
import org.lukawska.trainSmart.mailing.domain.valueObject.Attachment;
import org.lukawska.trainSmart.mailing.domain.valueObject.AttachmentType;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AttachmentValidationTest {

	private AttachmentValidation attachmentValidation;

	@BeforeEach
	void setUp() {
		long maxSize = 5 * 1024 * 1024;
		this.attachmentValidation = new AttachmentValidation(maxSize);
	}

	@Test
	void shouldValidateAttachmentsSuccess() {
		//given
		AttachmentType attachmentType = AttachmentType.values()[new Random().nextInt(AttachmentType.values().length)];
		String fileName = UUID.randomUUID() + "." + attachmentType.getExtension();
		Attachment attachment = new Attachment(fileName,
		                                       AttachmentType.PDF,
		                                       new ByteArrayResource(new byte[10]));
		List<Attachment> attachments = List.of(attachment);

		//when && then
		assertDoesNotThrow(() -> attachmentValidation.validateAttachments(attachments));
	}

	@Test
	void shouldThrowExceptionWhenAttachmentNameInvalid() {
		//given
		AttachmentType attachmentType = AttachmentType.values()[new Random().nextInt(AttachmentType.values().length)];
		Attachment attachment = new Attachment("",
		                                       attachmentType,
		                                       new ByteArrayResource(new byte[10]));
		List<Attachment> attachments = List.of(attachment);

		//when && then
		assertThatThrownBy(() -> attachmentValidation.validateAttachments(attachments))
				.isInstanceOf(RuntimeException.class)
				.hasMessage(ExceptionType.INVALID_ATTACHMENT_NAME.getMessage());
	}

	@Test
	void shouldThrowExceptionWhenAttachmentExtensionInvalid() {
		//given
		String fileName = UUID.randomUUID() + ".invalid";
		AttachmentType attachmentType = AttachmentType.values()[new Random().nextInt(AttachmentType.values().length)];
		Attachment attachment = new Attachment(fileName,
		                                       attachmentType,
		                                       new ByteArrayResource(new byte[10]));
		List<Attachment> attachments = List.of(attachment);

		//when && then
		assertThatThrownBy(() -> attachmentValidation.validateAttachments(attachments))
				.isInstanceOf(RuntimeException.class)
				.hasMessage(ExceptionType.INVALID_ATTACHMENT.getMessage());
	}

	@Test
	void shouldThrowExceptionWhenAttachmentTypeInvalid() {
		//given
		String fileName = UUID.randomUUID() + ".pdf";
		Attachment attachment = new Attachment(fileName,
		                                       null,
		                                       new ByteArrayResource(new byte[10]));
		List<Attachment> attachments = List.of(attachment);

		//when && then
		assertThatThrownBy(() -> attachmentValidation.validateAttachments(attachments))
				.isInstanceOf(RuntimeException.class)
				.hasMessage(ExceptionType.INVALID_ATTACHMENT.getMessage());
	}

	@Test
	void shouldThrowExceptionWhenAttachmentSizeToLarge(){
		//given
		AttachmentType attachmentType = AttachmentType.values()[new Random().nextInt(AttachmentType.values().length)];
		String fileName = UUID.randomUUID() + "." + attachmentType.getExtension();
		Attachment attachment = new Attachment(fileName,
		                                       AttachmentType.PDF,
		                                       new ByteArrayResource(new byte[100 * 1024 * 1024]));
		List<Attachment> attachments = List.of(attachment);

		//when && then
		assertThatThrownBy(() -> attachmentValidation.validateAttachments(attachments))
				.isInstanceOf(RuntimeException.class)
				.hasMessage(ExceptionType.ATTACHMENT_TOO_LARGE.getMessage());
	}
}
