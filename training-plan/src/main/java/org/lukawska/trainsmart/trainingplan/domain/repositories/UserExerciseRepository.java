package org.lukawska.trainsmart.trainingplan.domain.repositories;

import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface UserExerciseRepository extends JpaRepository<UserExercise, Long> {

    List<UserExercise> findAllByUserId(Long userId);

    List<UserExercise> findAllByUserIdAndEnabled(Long userId, boolean enabled);

    @Query("SELECT ue.exercise.name FROM UserExercise ue WHERE ue.user.id = :userId")
    List<String> findAllExerciseNamesByUserId(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE UserExercise ue SET ue.modifiedAt = :date, ue.enabled =
            CASE
                 WHEN ue.exercise.name IN :names THEN FALSE
                 ELSE TRUE
            END
            WHERE ue.user.id = :userId
            """)
    void updateEnabledByUserIdAndNames(@Param("userId") Long userId,
                                       @Param("names") List<String> names,
                                       @Param("date") Instant date);

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE UserExercise  ue SET ue.enabled = true , ue.modifiedAt = :date
            WHERE ue.user.id = :userId AND ue.enabled = false
            """)
    void enableAllByUserId(@Param("userId") Long userId, @Param("date") Instant date);

}
