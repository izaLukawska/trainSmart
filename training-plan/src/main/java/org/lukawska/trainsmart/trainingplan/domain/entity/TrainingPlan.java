package org.lukawska.trainsmart.trainingplan.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;

import java.util.ArrayList;
import java.util.List;

@Table(name = "training_plan")
@Entity
@NoArgsConstructor
@Getter
public class TrainingPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private TrainingType trainingType;

    @Enumerated(EnumType.STRING)
    private PlanDuration planDuration;

    private int daysPerWeek;

    @OneToMany(mappedBy = "trainingPlan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TrainingWeek> weeks = new ArrayList<>();

    public TrainingPlan(User user, TrainingType trainingType, PlanDuration planDuration, int daysPerWeek) {
        this.user = user;
        this.trainingType = trainingType;
        this.planDuration = planDuration;
        this.daysPerWeek = daysPerWeek;
    }

    public void addTrainingWeek(TrainingWeek trainingWeek) {
        weeks.add(trainingWeek);
    }
}
