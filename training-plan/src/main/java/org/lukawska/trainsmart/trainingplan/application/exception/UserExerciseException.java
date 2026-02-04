package org.lukawska.trainsmart.trainingplan.application.exception;

import lombok.Getter;

@Getter
public class UserExerciseException extends RuntimeException {

    private final ExceptionType exceptionType;

    public UserExerciseException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }

}
