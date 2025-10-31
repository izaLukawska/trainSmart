package org.lukawska.trainsmart.statements.presentation.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(StatementException.class)
    public ProblemDetail handleStatementException(StatementException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(exception.getExceptionType().getStatus());

        problemDetail.setDetail(exception.getMessage());
        problemDetail.setType(URI.create(String.format("/errors/%s", exception.getExceptionType()
                                                                              .name()
                                                                              .toLowerCase()
                                                                              .replaceAll("_", "-"))));

        return problemDetail;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation Failure");
        problemDetail.setType(URI.create("/errors/constraint-violation"));
        problemDetail.setDetail("Some fields violate the constraints.");
        problemDetail.setProperty("error", getConstraintViolation(ex));

        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception exception) {
        log.error("Unexpected error: {}", exception.getMessage(), exception);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setType(URI.create("/errors/internal-server-error"));
        problemDetail.setDetail("Unexpected error. Please contact the admin.");

        return problemDetail;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {


        ProblemDetail problem = ex.getBody();
        problem.setType(URI.create("/errors/method-argument-not-valid"));
        problem.setDetail("Request contains invalid fields");
        problem.setProperty("errors", getFieldErrors(ex));

        return ResponseEntity.status(status).headers(headers).body(problem);
    }

    private Map<String, String> getConstraintViolation(ConstraintViolationException ex) {
        return ex.getConstraintViolations()
                 .stream()
                 .collect(Collectors.toMap(constraintViolation -> constraintViolation.getPropertyPath().toString(),
                                           ConstraintViolation::getMessage,
                                           (oldMessage, newMessage) -> String.format("%s, %s",
                                                                                     oldMessage, newMessage)));
    }

    private Map<String, String> getFieldErrors(MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
                 .getFieldErrors()
                 .stream()
                 .collect(Collectors.toMap(FieldError::getField, fieldError -> {
                     String message = fieldError.getDefaultMessage();
                     return StringUtils.isNotBlank(message) ? message : "invalid value";
                 }, (existing, replacement) -> existing));
    }
}
