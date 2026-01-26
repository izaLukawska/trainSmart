package org.lukawska.trainsmart.healthsurvey.presentation.exception;

import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.healthsurvey.application.exception.HealthSurveyException;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class HealthSurveyExceptionHandler {

    @ExceptionHandler(HealthSurveyException.class)
    public ProblemDetail handleHealthSurveyException(HealthSurveyException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getExceptionType().getHttpStatus(),
                                                                       ex.getMessage());
        problemDetail.setTitle("Health survey exception");

        return problemDetail;
    }
}
