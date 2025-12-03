package org.lukawska.trainsmart.mailing.application.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ExceptionType {

    INVALID_ATTACHMENT_TYPE("Invalid attachment type.", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    INVALID_ATTACHMENT_EXTENSION("Invalid attachment extension.", HttpStatus.BAD_REQUEST),
    ATTACHMENT_TOO_LARGE("Attachment too large.", HttpStatus.PAYLOAD_TOO_LARGE),
    MAIL_SEND_ERROR("Mail sending failed.", HttpStatus.INTERNAL_SERVER_ERROR),
    MAIL_NOT_FOUND("Mail not found.", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus httpStatus;

}
