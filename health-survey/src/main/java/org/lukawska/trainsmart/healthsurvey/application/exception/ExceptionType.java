package org.lukawska.trainsmart.healthsurvey.application.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ExceptionType {

    HEALTH_SURVEY_ALREADY_EXISTS(HttpStatus.CONFLICT, "Health survey already exists"),
    HEALTH_SURVEY_NOT_FOUND(HttpStatus.NOT_FOUND, "Health survey does not exist");

    private final HttpStatus httpStatus;
    private final String message;

}
