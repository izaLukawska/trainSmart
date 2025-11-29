package org.lukawska.trainsmart.trainingplan.application.dto;

import jakarta.validation.constraints.NotNull;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;

public record TrainingPlanRequest(@NotNull TrainingType trainingType,
                                  @NotNull PlanDuration planDuration,
                                  int daysPerWeek) {}
