package org.lukawska.trainsmart.statements.presentation.exception;

import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class StatementsExceptionHandler {

    @ExceptionHandler(StatementException.class)
    public ProblemDetail handleStatementException(StatementException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(exception.getExceptionType().getStatus(),
                                                                       exception.getMessage());
        problemDetail.setTitle("Statement exception");

        return problemDetail;
    }
}
