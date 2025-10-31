package org.lukawska.trainsmart.statements.presentation.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
enum ProblemType {

    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation Failure", "/errors/constraint-violation"),
    METHOD_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST, "Argument not valid", "/errors/method-argument-not-valid"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "/errors/internal-server-error");

    private final HttpStatus status;

    private final String title;

    private final String typeUri;

}
