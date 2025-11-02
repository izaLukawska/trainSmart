package org.lukawska.trainsmart.openai.application.exception;

import lombok.Getter;

@Getter
public class OpenAiException extends RuntimeException {

    private final ExceptionType exceptionType;

    public OpenAiException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }
}
