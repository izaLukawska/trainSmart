package org.lukawska.trainsmart.purchase.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.purchase.domain.repository.TrainingPlanPurchaseRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class TrainingPlanPurchaseCleanupService {

    private final TrainingPlanPurchaseRepository purchaseRepository;

    @Transactional
    public void cleanupStalePurchases() {
        log.info("Deleting stale purchases");
        Instant oneWeekAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        purchaseRepository.deleteStalePendingPurchases(oneWeekAgo);
    }
}
