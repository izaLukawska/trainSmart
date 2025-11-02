package org.lukawska.trainsmart.openai.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ExceptionType {

    OPENAI_CLIENT_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "OpenAI request failed");

    private final HttpStatus httpStatus;
    private final String message;
}
