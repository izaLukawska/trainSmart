package org.lukawska.trainsmart.purchase.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.purchase.domain.repository.TrainingPlanPurchaseRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrainingPlanPurchaseCleanupServiceTest {

    @Mock
    private TrainingPlanPurchaseRepository purchaseRepository;

    @InjectMocks
    private TrainingPlanPurchaseCleanupService cleanupService;

    @Test
    void shouldDeleteStalePurchases() {
        //when
        cleanupService.cleanupStalePurchases();

        //then
        verify(purchaseRepository).deleteStalePendingPurchases(any(Instant.class));
    }
}
