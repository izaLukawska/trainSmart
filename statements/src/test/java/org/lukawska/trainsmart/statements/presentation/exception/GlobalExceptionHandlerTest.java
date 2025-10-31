package org.lukawska.trainsmart.statements.presentation.exception;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.statements.application.exception.ExceptionType;
import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lukawska.trainsmart.statements.testutil.ExceptionTestData.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void shouldHandleStatementExceptionWhenUserAgreementNotFound() {
        //given
        final StatementException statementException = new StatementException(ExceptionType.STATEMENT_NOT_FOUND);

        //when
        ProblemDetail result = exceptionHandler.handleStatementException(statementException);

        //then
        assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(result.getTitle()).isEqualTo("Statement exception");
        assertThat(result.getDetail()).isEqualTo(statementException.getMessage());
        assertThat(result.getType().getPath()).isEqualTo("/errors/statement-not-found");
    }

    @Test
    void shouldHandleConstraintViolation() {
        // given
        final ConstraintViolation<?> violation1 = mockViolation(randomText());
        final ConstraintViolation<?> violation2 = mockViolation(randomText());
        final ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation1, violation2));

        // when
        ProblemDetail result = exceptionHandler.handleConstraintViolation(ex);

        // then
        String violationProperty = GlobalExceptionHandler.VIOLATION_PROPERTY;
        @SuppressWarnings("unchecked")
        Map<String, String> violations = (Map<String, String>) Objects.requireNonNull(result.getProperties())
                                                                      .get(violationProperty);

        assertThat(violations).isNotNull()
                              .hasSize(2)
                              .containsEntry(violation1.getPropertyPath().toString(), violation1.getMessage())
                              .containsEntry(violation2.getPropertyPath().toString(), violation2.getMessage());
    }

    @Test
    void shouldHandleGenericException() {
        // given
        final Exception ex = new RuntimeException(UUID.randomUUID().toString());

        // when
        ProblemDetail result = exceptionHandler.handleGenericException(ex);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ProblemType.INTERNAL_ERROR.getStatus().value());
        assertThat(result.getTitle()).isEqualTo(ProblemType.INTERNAL_ERROR.getTitle());
        assertThat(result.getType().getPath()).isEqualTo(ProblemType.INTERNAL_ERROR.getTypeUri());
        assertThat(result.getDetail()).isEqualTo("Unexpected error occurred");
    }

    @Test
    void shouldHandleMethodArgumentNotValid() {
        // given
        final ProblemType problemType = ProblemType.METHOD_ARGUMENT_NOT_VALID;
        final String fieldErrorsProperty = GlobalExceptionHandler.FIELD_ERRORS_PROPERTY;
        final FieldError fieldError1 = randomFieldError();
        final FieldError fieldError2 = randomFieldError();
        final BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(mock(MethodParameter.class),
                                                                                 bindingResult);

        // when
        ProblemDetail result = (ProblemDetail)
                Objects.requireNonNull(exceptionHandler.handleMethodArgumentNotValid(ex,
                                                                                     new HttpHeaders(),
                                                                                     HttpStatus.BAD_REQUEST,
                                                                                     mock(WebRequest.class)))
                       .getBody();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(problemType.getStatus().value());
        assertThat(result.getTitle()).isEqualTo(problemType.getTitle());
        assertThat(result.getType().getPath()).isEqualTo(problemType.getTypeUri());
        assertThat(result.getProperties()).hasFieldOrProperty(fieldErrorsProperty);

        @SuppressWarnings("unchecked")
        Map<String, String> fieldErrors = Objects.requireNonNull(
                (Map<String, String>) Objects.requireNonNull(result.getProperties()).get(fieldErrorsProperty));

        assertThat(fieldErrors).isNotNull()
                               .hasSize(2)
                               .containsEntry(fieldError1.getField(), fieldError1.getDefaultMessage())
                               .containsEntry(fieldError2.getField(), fieldError2.getDefaultMessage());
    }
}
