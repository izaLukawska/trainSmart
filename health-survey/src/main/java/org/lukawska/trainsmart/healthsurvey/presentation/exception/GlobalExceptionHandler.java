package org.lukawska.trainsmart.healthsurvey.presentation.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lukawska.trainsmart.healthsurvey.application.exception.HealthSurveyException;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(HealthSurveyException.class)
    public ProblemDetail handleHealthSurveyException(HealthSurveyException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getExceptionType().getHttpStatus(),
                                                                       ex.getMessage());
        problemDetail.setTitle("Health survey exception");

        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException() {
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Constraint violation");
        problemDetail.setProperties(getConstraintViolation(ex));

        return problemDetail;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation failure");
        problemDetail.setProperties(getFieldErrors(ex));

        return ResponseEntity.status(status)
                             .headers(headers)
                             .body(problemDetail);
    }

    private Map<String, Object> getConstraintViolation(ConstraintViolationException ex) {
        return ex.getConstraintViolations()
                 .stream()
                 .collect(Collectors.toMap(constraintViolation -> constraintViolation.getPropertyPath().toString(),
                                           ConstraintViolation::getMessage,
                                           (oldMessage, newMessage) ->
                                                   String.format("%s, %s", oldMessage, newMessage)));
    }

    private Map<String, Object> getFieldErrors(MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
                 .getFieldErrors()
                 .stream()
                 .collect(Collectors.toMap(FieldError::getField, fieldError -> {
                     String message = fieldError.getDefaultMessage();
                     return StringUtils.isNotBlank(message) ? message : "invalid value";
                 }, (oldMessage, newMessage) -> String.format("%s, %s", oldMessage, newMessage)));
    }
}
