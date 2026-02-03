package org.lukawska.trainsmart.usermanagement.presentation.exception;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.usermanagement.application.exception.ExceptionType;
import org.lukawska.trainsmart.usermanagement.application.exception.UserManagementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;

class UserManagementExceptionHandlerTest {

    private final UserManagementExceptionHandler exceptionHandler = new UserManagementExceptionHandler();

    @Test
    void shouldReturnBadCredentialsException() {
        //given
        final BadCredentialsException exception = new BadCredentialsException("Invalid login");

        //when
        ProblemDetail result = exceptionHandler.handleBadCredentialsException(exception);

        //then
        assertThat(result.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(result.getDetail()).isEqualTo(exception.getMessage());
        assertThat(result.getTitle()).isEqualTo("Login invalid");
    }

    @Test
    void shouldReturnBadRequestException() {
        //given
        final ExceptionType exceptionType = ExceptionType.USER_ALREADY_EXISTS;
        final UserManagementException exception = new UserManagementException(exceptionType);

        //when
        ProblemDetail result = exceptionHandler.handleUserManagementException(exception);

        //then
        assertThat(result.getStatus()).isEqualTo(exceptionType.getHttpStatus().value());
        assertThat(result.getDetail()).isEqualTo(exception.getMessage());
        assertThat(result.getTitle()).isEqualTo("User management exception");
    }
}
