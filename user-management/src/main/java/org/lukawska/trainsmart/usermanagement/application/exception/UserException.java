package org.lukawska.trainsmart.usermanagement.application.exception;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException {

    private final ExceptionType exceptionType;

    public UserException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }
}
