package org.lukawska.trainsmart.usermanagement.application.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ExceptionType {

    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "User already exists"),
    EMAIL_TAKEN(HttpStatus.CONFLICT, "Email is taken"),
    MAIL_PROVIDER_NOT_FOUND(HttpStatus.BAD_REQUEST, "Mail provider not found"),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "Wrong email provided"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "Invalid password."),
    VERIFICATION_TOKEN_INVALID(HttpStatus.NOT_FOUND, " token is invalid"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid token"),
    ACCOUNT_NOT_ACTIVE(HttpStatus.FORBIDDEN, "Account not activated");

    private final HttpStatus httpStatus;
    private final String message;

}
