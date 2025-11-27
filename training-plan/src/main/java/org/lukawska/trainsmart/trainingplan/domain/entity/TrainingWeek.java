package org.lukawska.trainsmart.trainingplan.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Table(name = "training_week")
@Entity
@NoArgsConstructor
@Getter
public class TrainingWeek {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "training_plan_id", nullable = false)
    private TrainingPlan trainingPlan;

    private int weekIndex;

    @OneToMany(mappedBy = "trainingWeek", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TrainingBlock> trainingBlocks = new ArrayList<>();

    public TrainingWeek(TrainingPlan trainingPlan, int weekIndex, List<TrainingBlock> trainingBlocks) {
        this.trainingPlan = trainingPlan;
        this.weekIndex = weekIndex;
        this.trainingBlocks = trainingBlocks;
    }

    public TrainingWeek(TrainingPlan trainingPlan, int weekIndex) {
        this.trainingPlan = trainingPlan;
        this.weekIndex = weekIndex;
    }

    public void addTrainingBlock(TrainingBlock trainingBlock) {
        trainingBlocks.add(trainingBlock);
    }
}
