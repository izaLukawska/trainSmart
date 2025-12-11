package org.lukawska.trainsmart.trainingplan.application.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ExceptionType {

    TRAINING_PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "Training plan not found"),
    NOT_ENOUGH_MUSCLE_GROUPS(HttpStatus.UNPROCESSABLE_ENTITY, "Not enough muscle groups to generate a training plan"),
    NOT_ENOUGH_EXERCISES(HttpStatus.UNPROCESSABLE_ENTITY, "Not enough exercises to generate a training plan"),
    NO_FILTER_STRATEGY_MATCH(HttpStatus.BAD_REQUEST, "No strategy found.");

    private final HttpStatus httpStatus;
    private final String message;

}
