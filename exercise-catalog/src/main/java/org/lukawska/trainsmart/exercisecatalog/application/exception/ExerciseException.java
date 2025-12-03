package org.lukawska.trainsmart.exercisecatalog.application.exception;

import lombok.Getter;

@Getter
public class ExerciseException extends RuntimeException {

    private final ExceptionType exceptionType;

    public ExerciseException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }
}
