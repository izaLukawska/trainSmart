package org.lukawska.trainsmart.mailing.application.exception;

import lombok.Getter;

@Getter
public class MailingException extends RuntimeException {

    private final ExceptionType exceptionType;

    public MailingException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }
}
