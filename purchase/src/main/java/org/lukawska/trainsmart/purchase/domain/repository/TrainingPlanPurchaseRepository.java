package org.lukawska.trainsmart.purchase.domain.repository;

import org.lukawska.trainsmart.purchase.domain.entity.TrainingPlanPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainingPlanPurchaseRepository extends JpaRepository<TrainingPlanPurchase, Long> {

    Optional<TrainingPlanPurchase> findByPaymentCode(String paymentCode);

}
