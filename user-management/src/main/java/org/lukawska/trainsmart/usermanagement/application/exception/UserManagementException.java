package org.lukawska.trainsmart.usermanagement.application.exception;

import lombok.Getter;

@Getter
public class UserManagementException extends RuntimeException {

    private final ExceptionType exceptionType;

    public UserManagementException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }
}
