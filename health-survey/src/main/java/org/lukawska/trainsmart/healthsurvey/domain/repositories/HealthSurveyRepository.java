package org.lukawska.trainsmart.healthsurvey.domain.repositories;

import org.lukawska.trainsmart.healthsurvey.domain.entites.HealthSurvey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@Repository
public interface HealthSurveyRepository extends JpaRepository<HealthSurvey, Long>,
                                                RevisionRepository<HealthSurvey, Long, Integer> {

    Optional<HealthSurvey> findByUserId(Long userId);

    @Query("SELECT hs.injuries FROM HealthSurvey hs WHERE hs.user.id = :userId")
    Optional<Set<String>> findAllInjuriesByUserId(@Param("userId") Long userId);

    @Query("SELECT hs.injuries FROM HealthSurvey hs WHERE hs.user.id = :userId AND hs.injuriesUpdatedAt > :since")
    Optional<Set<String>> findAllInjuriesUpdatedAtAfter(@Param("userId") Long userId, @Param("since") Instant since);

}
