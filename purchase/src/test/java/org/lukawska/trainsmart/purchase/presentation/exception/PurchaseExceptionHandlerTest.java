package org.lukawska.trainsmart.purchase.presentation.exception;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.purchase.application.exception.PurchaseException;
import org.lukawska.trainsmart.purchase.application.exception.PurchaseExceptionType;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

class PurchaseExceptionHandlerTest {

    private final PurchaseExceptionHandler exceptionHandler = new PurchaseExceptionHandler();

    @Test
    void shouldHandlePurchaseException() {
        //given
        final PurchaseException exception = new PurchaseException(PurchaseExceptionType.PAYMENT_REQUIRED);

        //when
        ProblemDetail problemDetail = exceptionHandler.handlePurchaseException(exception);

        //then
        assertThat(problemDetail.getStatus()).isEqualTo(exception.getPurchaseExceptionType().getHttpStatus().value());
        assertThat(problemDetail.getDetail()).isEqualTo(exception.getMessage());
    }
}
