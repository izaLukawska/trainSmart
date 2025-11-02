package org.lukawska.trainsmart.statements.presentation.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

import static org.lukawska.trainsmart.statements.presentation.exception.ProblemDetailMapper.toProblemDetailFromProblemType;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(StatementException.class)
    public ProblemDetail handleStatementException(StatementException exception) {
        log.warn("Handling statement exception: {}", exception.getExceptionType().name());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(exception.getExceptionType().getStatus(),
                                                                       exception.getMessage());

        String path = "/errors/" + exception.getExceptionType().name().toLowerCase().replace('_', '-');
        problemDetail.setType(URI.create(path));
        problemDetail.setTitle("Statement exception");
        return problemDetail;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Handling constraint violation exception: {}", ex.getMessage(), ex);

        ProblemDetail problemDetail = toProblemDetailFromProblemType(ProblemType.VALIDATION_ERROR);
        problemDetail.setProperties(getConstraintViolation(ex));
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception exception) {
        log.error("Unexpected error: {}", exception.getMessage(), exception);

        return toProblemDetailFromProblemType(ProblemType.INTERNAL_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {

        log.warn("Handling method argument not valid exception: {}", ex.getMessage(), ex);

        ProblemDetail problemDetail = toProblemDetailFromProblemType(ProblemType.METHOD_ARGUMENT_NOT_VALID);
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
                                                                                     oldMessage, newMessage)));
    }

    private Map<String, Object> getFieldErrors(MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
                 .getFieldErrors()
                 .stream()
                 .collect(Collectors.toMap(FieldError::getField, fieldError -> {
                     String message = fieldError.getDefaultMessage();
                     return StringUtils.isNotBlank(message) ? message : "invalid value";
                 }, (existing, replacement) -> existing));
    }
}
