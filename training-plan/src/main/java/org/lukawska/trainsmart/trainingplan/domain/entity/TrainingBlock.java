package org.lukawska.trainsmart.trainingplan.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.WeekDay;

import java.util.ArrayList;
import java.util.List;

@Table(name = "training_block")
@Entity
@NoArgsConstructor
@Getter
public class TrainingBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private WeekDay weekDay;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "training_week_id")
    private TrainingWeek trainingWeek;

    @OneToMany(mappedBy = "trainingBlock", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BlockExercise> blockExercises = new ArrayList<>();

    public TrainingBlock(TrainingWeek trainingWeek) {
        this.trainingWeek = trainingWeek;
    }

    public void addBlockExercise(BlockExercise blockExercise) {
        blockExercises.add(blockExercise);
    }
}
