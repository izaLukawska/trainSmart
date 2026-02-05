package org.lukawska.trainsmart.purchase.infrastructure.event;

public record PurchaseCompletedEvent(Long planId, Long userId, String email) {}
