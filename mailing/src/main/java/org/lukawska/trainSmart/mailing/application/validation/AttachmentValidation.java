package org.lukawska.trainSmart.mailing.application.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.lukawska.trainSmart.mailing.application.exception.ExceptionType;
import org.lukawska.trainSmart.mailing.application.exception.RestException;
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
@RequiredArgsConstructor
public final class AttachmentValidation {

	@Value("${mail.attachments.max-size}")
	private final long maxAttachmentSize;

	public void validateAttachments(List<Attachment> attachments) {
		for (Attachment att : attachments) {
			validateAttachmentFileName(att);
			validateAttachmentExtension(att);
			validateAttachmentType(att);
			validateAttachmentSize(att);
		}
	}

	private void validateAttachmentExtension(Attachment att) {
		String extension = StringUtils.substringAfterLast(att.fileName(), ".");
		if (StringUtils.isBlank(extension) || !AttachmentType.isValidExtension(extension)) {
			throw new RestException(ExceptionType.INVALID_ATTACHMENT);
		}
	}

	private void validateAttachmentType(Attachment att) {
		if(Objects.isNull(att) || Objects.isNull(att.attachmentType())){
			throw new RestException(ExceptionType.INVALID_ATTACHMENT);
		}

		if (Arrays.stream(AttachmentType.values())
		          .noneMatch(type -> type.getMimeType().equals(att.attachmentType().getMimeType()))) {
			throw new RestException(ExceptionType.INVALID_ATTACHMENT);
		}
	}

	private void validateAttachmentFileName(Attachment att) {
		if (StringUtils.isBlank(att.fileName())) {
			throw new RestException(ExceptionType.INVALID_ATTACHMENT_NAME);
		}
	}

	private void validateAttachmentSize(Attachment att) {
		try {
			long size = att.source().getInputStream().transferTo(OutputStream.nullOutputStream());
			if (size > maxAttachmentSize) {
				throw new RestException(ExceptionType.ATTACHMENT_TOO_LARGE);
			}
		} catch (IOException e) {
			throw new RestException(ExceptionType.ATTACHMENT_IO_ERROR);
		}
	}
}
