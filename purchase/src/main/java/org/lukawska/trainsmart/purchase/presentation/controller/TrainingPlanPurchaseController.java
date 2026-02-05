package org.lukawska.trainsmart.purchase.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.purchase.api.PurchaseApi;
import org.lukawska.trainsmart.purchase.application.service.TrainingPlanPurchaseService;
import org.lukawska.trainsmart.purchase.model.PurchaseRequest;
import org.lukawska.trainsmart.purchase.model.PurchaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TrainingPlanPurchaseController implements PurchaseApi {

    private final TrainingPlanPurchaseService purchaseService;

    @Override
    public ResponseEntity<PurchaseResponse> checkoutPurchase(PurchaseRequest purchaseRequest) {
        log.info("Received checkout purchase request");
        return ResponseEntity.ok(purchaseService.purchaseCheckout(purchaseRequest));
    }

    @Override
    public ResponseEntity<PurchaseResponse> completePurchase(String paymentCode) {
        log.info("Received complete purchase request");
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseService.completePurchase(paymentCode));
    }
}
