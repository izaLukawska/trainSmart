package org.lukawska.trainsmart.exercisecatalog.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.ExerciseType;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.lukawska.trainsmart.sharedpersistence.infrastructure.audit.AuditableEntity;

@Table(name = "exercises", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "exercise_type"}))
@NoArgsConstructor
@Getter
@Entity
public class Exercise extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MuscleGroup muscleGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "exercise_type", nullable = false)
    private ExerciseType exerciseType;

    public Exercise(String name, MuscleGroup muscleGroup, ExerciseType exerciseType) {
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.exerciseType = exerciseType;
    }
}
