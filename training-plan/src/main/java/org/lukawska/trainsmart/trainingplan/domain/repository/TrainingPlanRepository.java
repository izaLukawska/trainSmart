package org.lukawska.trainsmart.trainingplan.domain.repository;

import org.lukawska.trainsmart.trainingplan.domain.entity.TrainingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Long> {
}
