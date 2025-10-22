package org.lukawska.trainsmart.statements.presentation.exception;

import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.lukawska.trainsmart.statements.presentation.dto.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalRestExceptionHandler {

    @ExceptionHandler(StatementException.class)
    public ResponseEntity<ExceptionResponse> handleRestException(StatementException exception) {
        log.error("Error with status: {}  and message: {} occured",
                  exception.getExceptionType().getStatus(), exception.getMessage());

        return ResponseEntity
                .status(exception.getExceptionType().getStatus())
                .body(new ExceptionResponse(
                        exception.getMessage(),
                        exception.getExceptionType().getStatus().value())
                     );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleUnexpectedException(Exception exception) {
        log.error("Unexpected server error occurred", exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse(
                        "Internal server error",
                        HttpStatus.INTERNAL_SERVER_ERROR.value())
                     );
    }
}
