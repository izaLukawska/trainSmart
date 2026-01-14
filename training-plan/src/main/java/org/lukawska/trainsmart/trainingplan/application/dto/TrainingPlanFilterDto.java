package org.lukawska.trainsmart.trainingplan.application.dto;

import lombok.Builder;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;

@Builder
public record TrainingPlanFilterDto(TrainingType trainingType, PlanDuration planDuration) {}
