package org.lukawska.trainsmart.mailing.presentation.exception;

import org.lukawska.trainsmart.mailing.application.exception.MailingException;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MailingExceptionHandler {

    @ExceptionHandler(MailingException.class)
    public ProblemDetail handleMailingException(MailingException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getExceptionType().getHttpStatus(),
                                                                       ex.getMessage());
        problemDetail.setTitle("Mailing exception");

        return problemDetail;
    }
}
