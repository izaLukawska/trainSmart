package org.lukawska.trainsmart.usermanagement.application.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ExceptionType {

    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "User already exists"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    EMAIL_TAKEN(HttpStatus.CONFLICT, "Email is taken");

    private final HttpStatus httpStatus;

    private final String message;

}
