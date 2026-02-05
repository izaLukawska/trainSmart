package org.lukawska.trainsmart.purchase.domain.repository;

import org.lukawska.trainsmart.purchase.domain.entity.TrainingPlanPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface TrainingPlanPurchaseRepository extends JpaRepository<TrainingPlanPurchase, Long> {

    Optional<TrainingPlanPurchase> findByPaymentCode(String paymentCode);

    @Modifying
    @Query("DELETE FROM TrainingPlanPurchase p WHERE p.createdAt < :threshold")
    void deleteStalePendingPurchases(@Param("threshold") Instant threshold);

}
