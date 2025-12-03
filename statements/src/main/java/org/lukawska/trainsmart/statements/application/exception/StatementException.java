package org.lukawska.trainsmart.statements.application.exception;

import lombok.Getter;

@Getter
public class StatementException extends RuntimeException {

    private final ExceptionType exceptionType;

    public StatementException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }
}
