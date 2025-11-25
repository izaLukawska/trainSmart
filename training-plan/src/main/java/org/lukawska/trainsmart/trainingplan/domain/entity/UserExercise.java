package org.lukawska.trainsmart.trainingplan.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lukawska.trainsmart.exercisecatalog.domain.entity.Exercise;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.sharedpersistence.infrastructure.base.BaseEntity;

import java.time.Instant;

@Entity
@NoArgsConstructor
@Table(name = "user_exercise", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "exercise_id"}))
@Getter
public class UserExercise extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    public boolean enabled = true;

    private Instant lastUsedAt;

    public UserExercise(User user, Exercise exercise) {
        this.user = user;
        this.exercise = exercise;
    }

    public void recordExerciseUse() {
        this.lastUsedAt = Instant.now();
    }
}
