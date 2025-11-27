package org.lukawska.trainsmart.trainingplan.application.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ExceptionType {

    NOT_ENOUGH_EXERCISES(HttpStatus.PRECONDITION_FAILED, "Not enough exercises to generate a training plan"),
    TRAINING_PLAN_CREATION_ERROR(HttpStatus.CONFLICT, "Error during training plan creation"),
    USER_EXERCISE_ALREADY_EXISTS(HttpStatus.CONFLICT, "Duplicate user exercise");

    private final HttpStatus httpStatus;
    private final String message;

}
