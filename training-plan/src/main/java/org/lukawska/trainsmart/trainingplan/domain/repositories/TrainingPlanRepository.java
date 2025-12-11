package org.lukawska.trainsmart.trainingplan.domain.repositories;

import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Long> {

    @Query("SELECT MAX(tp.createdAt) FROM TrainingPlan tp WHERE tp.user.id = :userId")
    Optional<Instant> findMaxCreatedAtByUserId(@Param("userId") Long userId);

    ;

}
