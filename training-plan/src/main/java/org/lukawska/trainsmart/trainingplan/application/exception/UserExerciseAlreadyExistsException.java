package org.lukawska.trainsmart.trainingplan.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserExerciseAlreadyExistsException extends RuntimeException {

    public UserExerciseAlreadyExistsException(Long userId) {
        super(String.format("Duplicate exercise for user with ID: %d", userId));
    }
}
