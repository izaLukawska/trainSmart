package org.lukawska.trainsmart.usermanagement.application.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
@Getter
public class AuthorizationException extends RuntimeException {

    private static final String BASE_MESSAGE = "INVALID TOKEN";

    public AuthorizationException() {
        super(BASE_MESSAGE);
    }
}
