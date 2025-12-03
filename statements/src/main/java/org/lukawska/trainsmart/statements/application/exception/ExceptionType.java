package org.lukawska.trainsmart.statements.application.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ExceptionType {

    STATEMENT_ACCEPTANCE_REQUIRED("Statement acceptance required", HttpStatus.NOT_ACCEPTABLE),
    STATEMENT_NOT_FOUND("Statement not found", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus status;

}
