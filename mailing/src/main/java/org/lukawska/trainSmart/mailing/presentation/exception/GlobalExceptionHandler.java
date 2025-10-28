package org.lukawska.trainSmart.mailing.presentation.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainSmart.mailing.application.exception.MailingException;
import org.lukawska.trainSmart.mailing.presentation.dto.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MailingException.class)
    public ResponseEntity<ExceptionResponse> handleMailingException(MailingException ex) {
        log.error("Caught mailing exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(ex.getExceptionType().getHttpStatus())
                             .body(new ExceptionResponse(ex.getMessage(),
                                                         ex.getExceptionType().getHttpStatus().value()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations()
                           .stream()
                           .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                           .collect(Collectors.joining(", "));

        log.error("Constraint violation: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(new ExceptionResponse(message, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                           .getFieldErrors()
                           .stream()
                           .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                           .collect(Collectors.joining(", "));

        log.error("Invalid request: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(new ExceptionResponse(message, HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(new ExceptionResponse(ex.getMessage(),
                                                         HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
