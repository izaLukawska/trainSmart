package org.lukawska.trainsmart.trainingplan.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.WeekDay;

import java.util.List;

public record TrainingPlanRequest(@NotNull TrainingType trainingType,
                                  @NotNull PlanDuration planDuration,
                                  @Size(min = 1, max = 7) List<@NotNull WeekDay> preferredDays) {}
