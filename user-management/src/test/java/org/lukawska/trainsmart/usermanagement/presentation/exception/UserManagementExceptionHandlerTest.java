package org.lukawska.trainsmart.usermanagement.presentation.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.sharedpersistence.application.exception.UserNotFoundException;
import org.lukawska.trainsmart.usermanagement.application.exception.ExceptionType;
import org.lukawska.trainsmart.usermanagement.application.exception.UserManagementException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserManagementExceptionHandlerTest {

    private final UserManagementExceptionHandler exceptionHandler = new UserManagementExceptionHandler();

    @Test
    void shouldHandleUserNotFoundException() {
        //given
        final UserNotFoundException exception = new UserNotFoundException();

        //when
        ProblemDetail result = exceptionHandler.handleUserNotFoundException(exception);

        //then
        ProblemDetailAssert.then(result)
                           .isNotNull()
                           .hasTitle("User not found")
                           .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldUserManagementExerciseExceptionWhenExerciseNotFound() {
        //given
        final UserManagementException exception = new UserManagementException(ExceptionType.USER_ALREADY_EXISTS);

        //when
        ProblemDetail result = exceptionHandler.handleUserManagementException(exception);

        //then
        ProblemDetailAssert.then(result)
                           .isNotNull()
                           .hasStatus(exception.getExceptionType().getHttpStatus())
                           .hasDetail(exception.getMessage())
                           .hasTitle("User management exception");
    }

    @Test
    void shouldHandleConstraintViolation() {
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
    void shouldHandleBadCredentialsException() {
        //given
        final BadCredentialsException exception = new BadCredentialsException("Invalid login");

        //when
        ProblemDetail result = exceptionHandler.handleBadCredentialsException(exception);

        //then
        ProblemDetailAssert.then(result)
                           .isNotNull()
                           .hasTitle("Login invalid")
                           .hasStatus(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldHandleMethodArgumentNotValidAndUseDefaultMessageIfFieldErrorMessageNotPresent() {
        // given
        final FieldError fieldError1 = new FieldError("object", "name", "must be provided");
        final BindingResult bindingResult = mock(BindingResult.class);
        final MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1));
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
                           .hasFieldErrorProperty(fieldError1);
    }

    private static ConstraintViolation<?> mockViolation(String message) {
        Path path = mock(Path.class);
        doReturn(UUID.randomUUID().toString()).when(path).toString();
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn(message);

        return violation;
    }
}
