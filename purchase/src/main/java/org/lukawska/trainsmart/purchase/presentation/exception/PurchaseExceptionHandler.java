package org.lukawska.trainsmart.purchase.presentation.exception;

import org.lukawska.trainsmart.purchase.application.exception.PurchaseException;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PurchaseExceptionHandler {

    @ExceptionHandler(PurchaseException.class)
    public ProblemDetail handlePurchaseException(PurchaseException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getPurchaseExceptionType().getHttpStatus(),
                                                                       ex.getMessage());
        problemDetail.setTitle("Purchase exception");

        return problemDetail;
    }
}
