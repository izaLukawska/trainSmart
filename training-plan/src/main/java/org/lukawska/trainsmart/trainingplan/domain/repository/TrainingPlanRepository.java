package org.lukawska.trainsmart.trainingplan.domain.repository;

import org.lukawska.trainsmart.trainingplan.domain.entity.TrainingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Long> {

    Optional<Instant> findTopCreatedAtByUserIdOrderByCreatedAtDesc(Long userId);
}
