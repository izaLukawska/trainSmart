package exercisecatalog.domain.entity;

import exercisecatalog.domain.valueObject.ExerciseType;
import exercisecatalog.domain.valueObject.MuscleGroup;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sharedpersistence.infrastructure.base.BaseEntity;

@Table(name = "exercises", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "exercise_type"}))
@NoArgsConstructor
@Getter
@Entity
public class Exercise extends BaseEntity {

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
