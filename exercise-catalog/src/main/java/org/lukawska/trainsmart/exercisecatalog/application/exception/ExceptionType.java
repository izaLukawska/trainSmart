package org.lukawska.trainsmart.exercisecatalog.application.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ExceptionType {

    EXERCISE_NOT_FOUND(HttpStatus.NOT_FOUND, "Exercise does not exist"),
    EXERCISE_ALREADY_EXISTS(HttpStatus.CONFLICT, "Duplicate exercise (name with type)");

    private final HttpStatus httpStatus;
    private final String message;
}
