package org.lukawska.trainsmart.trainingplan.domain.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lukawska.trainsmart.exercisecatalog.domain.entities.Exercise;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObjects.ExerciseType;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.sharedpersistence.infrastructure.audit.AuditableEntity;

import java.time.Instant;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_exercise", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "exercise_id"}))
@Getter
public class UserExercise extends AuditableEntity {

    public boolean enabled = true;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;
    private Instant lastUsedAt;

    public UserExercise(User user, Exercise exercise) {
        this.user = user;
        this.exercise = exercise;
    }

    public void recordExerciseUse() {
        this.lastUsedAt = Instant.now();
    }

    public boolean isBarbellExercise() {
        return this.exercise.getExerciseType().equals(ExerciseType.BARBELL);
    }
}
