package org.lukawska.trainsmart.trainingplan.domain.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.IntensityLevel;

@Table(name = "block_exercise")
@Entity
@NoArgsConstructor
@Getter
public class BlockExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_block_id", nullable = false)
    private TrainingBlock trainingBlock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_exercise_id", nullable = false)
    private UserExercise userExercise;

    private int sets;

    private int reps;

    @Enumerated(EnumType.STRING)
    private IntensityLevel intensity;

    @Setter
    private Double loadPercent;

    @Setter
    private Double load;

    @Builder
    public BlockExercise(TrainingBlock trainingBlock, UserExercise userExercise, int sets, int reps,
                         IntensityLevel intensity, Double loadPercent, Double load) {
        this.trainingBlock = trainingBlock;
        this.userExercise = userExercise;
        this.sets = sets;
        this.reps = reps;
        this.intensity = intensity;
        this.loadPercent = loadPercent;
        this.load = load;
    }
}
