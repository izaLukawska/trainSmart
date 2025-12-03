package org.lukawska.trainsmart.openai.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class OpenAiException extends RuntimeException {

    public OpenAiException() {
        super("OpenAI request failed");
    }
}
