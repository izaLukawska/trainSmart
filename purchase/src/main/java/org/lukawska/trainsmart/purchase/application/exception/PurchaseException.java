package org.lukawska.trainsmart.purchase.application.exception;

import lombok.Getter;

@Getter
public class PurchaseException extends RuntimeException {

    private final PurchaseExceptionType purchaseExceptionType;

    public PurchaseException(PurchaseExceptionType purchaseExceptionType) {
        super(purchaseExceptionType.getMessage());
        this.purchaseExceptionType = purchaseExceptionType;
    }
}
