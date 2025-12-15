package org.lukawska.trainsmart.trainingplan.application.dto.request;

import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;

public record TrainingPlanFilterRequest(TrainingType trainingType, PlanDuration planDuration) {}
