package org.lukawska.trainsmart.statements.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionType {

    USER_AGREEMENT_NOT_FOUND("User agreement not found.", HttpStatus.NOT_FOUND),
    USER_AGREEMENT_ALREADY_EXISTS("User agreement already exists", HttpStatus.CONFLICT),
    STATEMENT_ACCEPTANCE_REQUIRED("Statement acceptance required", HttpStatus.NOT_ACCEPTABLE),
    STATEMENT_NOT_FOUND("Statement not found", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus status;

}
