package org.lukawska.trainsmart.healthsurvey.application.exception;

import lombok.Getter;

@Getter
public class HealthSurveyException extends RuntimeException {

    private final ExceptionType exceptionType;

    public HealthSurveyException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }
}
