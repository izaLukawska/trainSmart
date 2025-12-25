package org.lukawska.trainsmart.usermanagement.presentation.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.StringUtils;
import org.lukawska.trainsmart.sharedpersistence.application.exception.UserNotFoundException;
import org.lukawska.trainsmart.usermanagement.application.exception.AuthorizationException;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class UserManagementExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFoundException(UserNotFoundException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problemDetail.setTitle("User not found");

        return problemDetail;
    }

    @ExceptionHandler(AuthorizationException.class)
    public ProblemDetail handleUserNotFoundException(AuthorizationException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
        problemDetail.setTitle("Authorization exception");

        return problemDetail;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Constraint violation");
        problemDetail.setProperties(getConstraintViolation(ex));

        return problemDetail;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
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
                                           (oldMessage, newMessage) -> String.format("%s, %s",
                                                                                     oldMessage,
                                                                                     newMessage)));
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
