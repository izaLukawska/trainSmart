package org.lukawska.trainsmart.health_survey.domain.repositories;

import org.lukawska.trainsmart.health_survey.domain.entites.HealthSurvey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HealthSurveyRepository extends JpaRepository<HealthSurvey, Long> {

    Optional<HealthSurvey> findByUserId(Long userId);

}
