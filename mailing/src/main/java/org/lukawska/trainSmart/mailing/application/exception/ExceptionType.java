package org.lukawska.trainSmart.mailing.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionType {

	ATTACHMENT_IO_ERROR("Attachment processing error.", HttpStatus.INTERNAL_SERVER_ERROR),
	VALIDATION_ERROR("Invalid request.", HttpStatus.BAD_REQUEST),
	INVALID_ATTACHMENT_NAME("Invalid attachment name.", HttpStatus.BAD_REQUEST),
	INVALID_ATTACHMENT("Invalid attachment type.", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
	ATTACHMENT_TOO_LARGE("Attachment too large.", HttpStatus.PAYLOAD_TOO_LARGE),
	MAIL_AUTH_ERROR("Mail authentication failed.", HttpStatus.UNAUTHORIZED),
	MAIL_SEND_ERROR("Mail sending failed.", HttpStatus.INTERNAL_SERVER_ERROR),
	MAIL_NOT_FOUND("Mail not found.", HttpStatus.NOT_FOUND);

	private final String message;
	private final HttpStatus httpStatus;

}
