package org.lukawska.trainsmart.exercisecatalog.presentation.exception;

import org.lukawska.trainsmart.exercisecatalog.application.exception.ExerciseException;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExerciseExceptionHandler {

    @ExceptionHandler(ExerciseException.class)
    public ProblemDetail handleHealthSurveyException(ExerciseException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getExceptionType().getHttpStatus(),
                                                                       ex.getMessage());
        problemDetail.setTitle("Exercise exception");

        return problemDetail;
    }
}
