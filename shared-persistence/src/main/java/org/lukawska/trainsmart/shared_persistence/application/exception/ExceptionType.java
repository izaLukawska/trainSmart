package org.lukawska.trainsmart.shared_persistence.application.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionType {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found");

    private final HttpStatus httpStatus;
    private final String message;
}
