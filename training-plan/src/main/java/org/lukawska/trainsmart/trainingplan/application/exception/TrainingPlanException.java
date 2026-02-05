package org.lukawska.trainsmart.trainingplan.application.exception;

import lombok.Getter;

@Getter
public class TrainingPlanException extends RuntimeException {

    private final ExceptionType exceptionType;

    public TrainingPlanException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }
}
