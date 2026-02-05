package org.lukawska.trainsmart.purchase.application.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum PurchaseExceptionType {

    PURCHASE_INVALID(HttpStatus.UNPROCESSABLE_ENTITY, "Purchase failed or completed"),
    PURCHASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Error while processing purchase"),
    PAYMENT_REQUIRED(HttpStatus.PAYMENT_REQUIRED, "Payment is required");

    private final HttpStatus httpStatus;
    private final String message;
}
