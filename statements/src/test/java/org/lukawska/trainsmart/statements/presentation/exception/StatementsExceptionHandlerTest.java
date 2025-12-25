package org.lukawska.trainsmart.statements.presentation.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
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
import static org.mockito.Mockito.*;

class StatementsExceptionHandlerTest {

    private final StatementsExceptionHandler exceptionHandler = new StatementsExceptionHandler();

    @Test
    void shouldHandleStatementExceptionWhenStatementNotFound() {
        //given
        final StatementException statementException = new StatementException(ExceptionType.STATEMENT_NOT_FOUND);

        //when
        ProblemDetail result = exceptionHandler.handleStatementException(statementException);

        //then
        ProblemDetailAssert.then(result)
                           .isNotNull()
                           .hasStatus(HttpStatus.NOT_FOUND)
                           .hasDetail(statementException.getMessage())
                           .hasTitle("Statement exception");
    }

    @Test
    void shouldHandleConstraintViolationException() {
        //given
        final ConstraintViolation<?> violation1 = mockViolation(UUID.randomUUID().toString());
        final ConstraintViolation<?> violation2 = mockViolation(UUID.randomUUID().toString());
        final ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation1, violation2));

        //when
        ProblemDetail result = exceptionHandler.handleConstraintViolation(ex);

        //then
        ProblemDetailAssert.then(result)
                           .isNotNull()
                           .hasStatus(HttpStatus.BAD_REQUEST)
                           .hasTitle("Constraint violation")
                           .hasConstraintViolation(violation1)
                           .hasConstraintViolation(violation2);
    }

    @Test
    void shouldHandleMethodArgumentNotValidException() {
        // given
        final FieldError fieldError1 = new FieldError("object", "age", "must be 18");
        final FieldError fieldError2 = new FieldError("object", "username", "must not be blank");

        final BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        final MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(ex.getMessage()).thenReturn("Validation failed");

        // when
        ResponseEntity<Object> response = exceptionHandler.handleMethodArgumentNotValid(
                ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, mock(WebRequest.class));

        // then
        assertThat(response).isNotNull();
        ProblemDetail problemDetail = (ProblemDetail) response.getBody();
        ProblemDetailAssert.then(problemDetail)
                           .isNotNull()
                           .hasStatus(HttpStatus.BAD_REQUEST)
                           .hasTitle("Validation failure")
                           .hasFieldErrorProperty(fieldError1)
                           .hasFieldErrorProperty(fieldError2);
    }

    private ConstraintViolation<?> mockViolation(String message) {
        Path path = mock(Path.class);
        doReturn(UUID.randomUUID().toString()).when(path).toString();
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn(message);

        return violation;
    }
}
