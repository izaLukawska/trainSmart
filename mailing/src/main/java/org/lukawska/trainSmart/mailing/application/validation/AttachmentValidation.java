package org.lukawska.trainSmart.mailing.application.validation;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainSmart.mailing.application.exception.ExceptionType;
import org.lukawska.trainSmart.mailing.application.exception.RestException;
import org.lukawska.trainSmart.mailing.domain.entities.Attachment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public final class AttachmentValidation {

	@Value("${mailing.max-attachment-size}")
	private final long maxAttachmentSize;

	private static final List<String> ALLOWED_MIME_TYPES = List.of(
			"application/pdf",
			"application/vnd.ms-excel",
			"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");


	public void validateAttachments(List<Attachment> attachments) throws IOException {
		for (Attachment att : attachments) {
			validateMimeType(att);
			validateAttachmentSize(att);
		}
	}

	private void validateMimeType(Attachment attachment) {
		if (!ALLOWED_MIME_TYPES.contains(attachment.mimeType())) {
			throw new RestException(ExceptionType.INVALID_ATTACHMENT_TYPE);
		}
	}

	private void validateAttachmentSize(Attachment attachment) throws IOException {
		long size = attachment.source().getInputStream().transferTo(OutputStream.nullOutputStream());
		if (size > maxAttachmentSize) {
			throw new RestException(ExceptionType.ATTACHMENT_TOO_LARGE);
		}
	}
}
