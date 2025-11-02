package org.lukawska.trainsmart.statements.presentation.exception;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.statements.application.exception.ExceptionType;
import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lukawska.trainsmart.statements.testutil.ExceptionTestData.mockViolation;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void shouldHandleStatementExceptionWhenStatementNotFound() {
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
    void shouldHandleGenericException() {
        //given
        ProblemType problemType = ProblemType.INTERNAL_ERROR;
        Exception exception = new Exception(UUID.randomUUID().toString());

        //when
        ProblemDetail result = exceptionHandler.handleGenericException(exception);

        //then
        assertThat(result.getStatus()).isEqualTo(problemType.getStatus().value());
        assertThat(result.getType().getPath()).isEqualTo(problemType.getTypeUri());
        assertThat(result.getTitle()).isEqualTo(problemType.getTitle());
    }

    @Test
    void shouldHandleConstraintViolation() {
        //given
        final ProblemType problemType = ProblemType.VALIDATION_ERROR;

        final ConstraintViolation<?> violation1 = mockViolation(UUID.randomUUID().toString());
        final ConstraintViolation<?> violation2 = mockViolation(UUID.randomUUID().toString());
        final ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation1, violation2));

        //when
        ProblemDetail result = exceptionHandler.handleConstraintViolation(ex);

        //then
        assertThat(result.getStatus()).isEqualTo(problemType.getStatus().value());
        assertThat(result.getType().getPath()).isEqualTo(problemType.getTypeUri());
        assertThat(result.getTitle()).isEqualTo(problemType.getTitle());
        assertThat(result.getProperties()).containsEntry(violation1.getPropertyPath().toString(),
                                                         violation1.getMessage());
        assertThat(result.getProperties()).containsEntry(violation2.getPropertyPath().toString(),
                                                         violation2.getMessage());
    }


    @Test
    void shouldHandleMethodArgumentNotValidAndUseDefaultMessageIfFieldErrorMessageNotPresent() {
        // given
        final ProblemType problemType = ProblemType.METHOD_ARGUMENT_NOT_VALID;
        final FieldError fieldError1 = new FieldError("object", "age", "");
        final FieldError fieldError2 = new FieldError("object", "username", "must not be blank");

        final BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        final MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(ex.getMessage()).thenReturn("Validation failed");

        // when
        ResponseEntity<Object> response = exceptionHandler.handleMethodArgumentNotValid(ex,
                                                                                        new HttpHeaders(),
                                                                                        problemType.getStatus(),
                                                                                        mock(WebRequest.class));

        // then
        assertThat(response).isNotNull();

        ProblemDetail problemDetail = (ProblemDetail) response.getBody();
        assertThat(problemDetail).isNotNull();
        assertThat(problemDetail.getTitle()).isEqualTo(problemType.getTitle());
        assertThat(problemDetail.getStatus()).isEqualTo(problemType.getStatus().value());
        assertThat(problemDetail.getProperties()).containsEntry(fieldError1.getField(), "invalid value")
                                                 .containsEntry(fieldError2.getField(),
                                                                fieldError2.getDefaultMessage());
    }
}
