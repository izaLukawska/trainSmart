package org.lukawska.trainsmart.trainingplan.application.generation;

import org.lukawska.trainsmart.trainingplan.domain.entity.TrainingPlan;

public interface TrainingPlanStrategy {

    TrainingPlan generateTrainingPlan(TrainingPlanContext trainingPlanContext);
}
