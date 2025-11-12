package org.lukawska.trainsmart.healthsurvey.domain.repositories;

import org.lukawska.trainsmart.healthsurvey.domain.entites.HealthSurvey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HealthSurveyRepository extends JpaRepository<HealthSurvey, Long>,
                                                RevisionRepository<HealthSurvey, Long, Integer> {

    Optional<HealthSurvey> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

}
