package org.lukawska.trainSmart.mailing.application.validation;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.lukawska.trainSmart.mailing.application.exception.ExceptionType;
import org.lukawska.trainSmart.mailing.application.exception.MailingException;
import org.lukawska.trainSmart.mailing.domain.valueObject.Attachment;
import org.lukawska.trainSmart.mailing.domain.valueObject.AttachmentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public final class AttachmentValidation {

	@Value("${mail.attachments.max-size}")
	private long maxAttachmentSize;

	public void validateAttachments(List<Attachment> attachments) {
		for (Attachment att : attachments) {
			validateAttachmentType(att);
			validateAttachmentFileName(att);
			validateAttachmentExtension(att);
			validateAttachmentSize(att);
		}
	}

	private void validateAttachmentType(Attachment att) {
		if (Objects.isNull(att) || Objects.isNull(att.attachmentType())) {
			throw new MailingException(ExceptionType.MISSING_ATTACHMENT);
		}

		if (Arrays.stream(AttachmentType.values())
		          .noneMatch(type -> type.getMimeType().equals(att.attachmentType().getMimeType()))) {
			throw new MailingException(ExceptionType.INVALID_ATTACHMENT);
		}
	}

	private void validateAttachmentFileName(Attachment att) {
		if (StringUtils.isBlank(att.fileName())) {
			throw new MailingException(ExceptionType.INVALID_ATTACHMENT_NAME);
		}
	}

	private void validateAttachmentExtension(Attachment att) {
		String extension = StringUtils.substringAfterLast(att.fileName(), ".");
		if (StringUtils.isBlank(extension) || !isValidExtension(extension)) {
			throw new MailingException(ExceptionType.INVALID_ATTACHMENT_EXTENSION);
		}
	}

	private void validateAttachmentSize(Attachment att) {
		try {
			long size = att.source().getInputStream().transferTo(OutputStream.nullOutputStream());
			if (size > maxAttachmentSize) {
				throw new MailingException(ExceptionType.ATTACHMENT_TOO_LARGE);
			}
		} catch (IOException e) {
			throw new MailingException(ExceptionType.ATTACHMENT_IO_ERROR);
		}
	}

	private boolean isValidExtension(String extension) {
		for (AttachmentType type : AttachmentType.values()) {
			if (type.getExtension().equalsIgnoreCase(extension)) {
				return true;
			}
		}
		return false;
	}
}
