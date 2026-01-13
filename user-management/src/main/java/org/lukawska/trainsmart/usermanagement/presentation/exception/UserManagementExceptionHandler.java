package org.lukawska.trainsmart.usermanagement.presentation.exception;

import org.lukawska.trainsmart.usermanagement.application.exception.UserManagementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class UserManagementExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentialsException(BadCredentialsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problemDetail.setTitle("Login invalid");

        return problemDetail;
    }

    @ExceptionHandler(UserManagementException.class)
    public ProblemDetail handleUserManagementException(UserManagementException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(exception.getExceptionType().getHttpStatus(),
                                                                       exception.getMessage());
        problemDetail.setTitle("User management exception");

        return problemDetail;
    }
}
