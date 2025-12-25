package org.lukawska.trainsmart.usermanagement.application.exception;

import lombok.Getter;

@Getter
public class AuthorizationException extends RuntimeException {

    private final ExceptionType exceptionType;

    public AuthorizationException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }
}
