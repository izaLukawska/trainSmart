package org.lukawska.trainsmart.gymfinder.presentation.exception;

import org.lukawska.trainsmart.gymfinder.application.exception.GymSearchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GymFinderExceptionHandler {

    @ExceptionHandler(GymSearchException.class)
    public ProblemDetail handleGymSearchException(GymSearchException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        problemDetail.setTitle("Gym search exception");

        return problemDetail;
    }
}
