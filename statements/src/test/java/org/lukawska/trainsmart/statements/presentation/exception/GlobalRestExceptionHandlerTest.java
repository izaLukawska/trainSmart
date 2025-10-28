package org.lukawska.trainsmart.statements.presentation.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.statements.application.exception.ExceptionType;
import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.lukawska.trainsmart.statements.presentation.dto.ExceptionResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalRestExceptionHandlerTest {

    private GlobalRestExceptionHandler handler;

    @Test
    void shouldReturn404ForUserNotFoundWhenHandleRestException() {
        // given
        handler = new GlobalRestExceptionHandler();
        final StatementException exception = new StatementException(ExceptionType.USER_NOT_FOUND);

        // when
        ResponseEntity<ExceptionResponse> response = handler.handleRestException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().message()).isEqualTo(ExceptionType.USER_NOT_FOUND.getMessage());
        assertThat(response.getBody().status()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldReturn500WhenHandleUnexpectedException_() {
        handler = new GlobalRestExceptionHandler();
        final Exception exception = new RuntimeException(UUID.randomUUID().toString());

        // WHEN
        ResponseEntity<ExceptionResponse> response = handler.handleUnexpectedException(exception);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().message()).isEqualTo("Internal server error");
    }

    @Test
    void shouldReturn400WithMessageWhenHandleValidationException_() {
        //given
        handler = new GlobalRestExceptionHandler();
        final BindingResult bindingResult = mock(BindingResult.class);
        final FieldError fieldError = new FieldError("objectName", "fieldName", "is required");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        String expectedMessage = "fieldName: is required";
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(mock(MethodParameter.class),
                                                                                        bindingResult);

        //when
        ResponseEntity<ExceptionResponse> response = handler.handleValidationException(exception);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().message()).isEqualTo(expectedMessage);
    }

    @Test
    void _shouldReturn400WithMessageWhenHandleConstraintViolation() {
        //given
        handler = new GlobalRestExceptionHandler();
        final ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        final Path path = mock(Path.class);
        final String field = "field";
        final String message = "invalid value";
        when(violation.getPropertyPath()).thenReturn(path);
        when(path.toString()).thenReturn(field);
        when(violation.getMessage()).thenReturn(message);

        ConstraintViolationException exception = new ConstraintViolationException(Set.of(violation));
        final String expectedMessage = field + ": " + message;

        //when
        ResponseEntity<ExceptionResponse> response = handler.handleConstraintViolation(exception);

        //then
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().message()).isEqualTo(expectedMessage);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
