package org.lukawska.trainsmart.purchase.domain.valueObject;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public enum PurchaseStatus {

    FAILED, PENDING, COMPLETED

}
