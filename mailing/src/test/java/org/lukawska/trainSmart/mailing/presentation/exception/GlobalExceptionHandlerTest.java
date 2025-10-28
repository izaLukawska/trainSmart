package org.lukawska.trainSmart.mailing.presentation.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainSmart.mailing.application.exception.ExceptionType;
import org.lukawska.trainSmart.mailing.application.exception.MailingException;
import org.lukawska.trainSmart.mailing.presentation.dto.ExceptionResponse;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @Test
    void shouldThrow404UserNotFoundWhenHandleMailingException() {
        //given
        exceptionHandler = new GlobalExceptionHandler();
        final MailingException exception = mock(MailingException.class);

        when(exception.getMessage()).thenReturn("Mail not found");
        when(exception.getExceptionType()).thenReturn(ExceptionType.MAIL_NOT_FOUND);

        //when
        ResponseEntity<ExceptionResponse> result = exceptionHandler.handleMailingException(exception);

        //then
        assertThat(result.getStatusCode().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
        Assertions.assertNotNull(result.getBody());
        assertThat(result.getBody().message()).isEqualTo("Mail not found");
    }

    @Test
    void shouldReturn400WithMessageWhenHandleValidationException() {
        //given
        exceptionHandler = new GlobalExceptionHandler();
        final BindingResult bindingResult = mock(BindingResult.class);
        final FieldError fieldError = new FieldError("objectName", "fieldName", "is required");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        String expectedMessage = "fieldName: is required";
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(mock(MethodParameter.class),
                                                                                        bindingResult);

        //when
        ResponseEntity<ExceptionResponse> response = exceptionHandler.handleValidationException(exception);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().message()).isEqualTo(expectedMessage);
    }

    @Test
    void shouldReturn400WithMessageWhenHandleConstraintViolation() {
        //given
        exceptionHandler = new GlobalExceptionHandler();
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
        ResponseEntity<ExceptionResponse> response = exceptionHandler.handleConstraintViolationException(exception);

        //then
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().message()).isEqualTo(expectedMessage);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturn500WithMessageWhenUnexpectedError() {
        //given
        exceptionHandler = new GlobalExceptionHandler();
        final Exception exception = new Exception("unknown");
        //when
        ResponseEntity<ExceptionResponse> result = exceptionHandler.handleGenericException(exception);

        //then
        Assertions.assertNotNull(result.getBody());
        assertThat(result.getBody().message()).isEqualTo(exception.getMessage());
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
