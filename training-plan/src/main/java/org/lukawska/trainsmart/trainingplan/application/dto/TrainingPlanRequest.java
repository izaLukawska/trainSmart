package org.lukawska.trainsmart.trainingplan.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.lukawska.trainsmart.trainingplan.application.preparation.validation.ConsistentPlanRequest;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.WeekDay;

import java.util.List;

@ConsistentPlanRequest
public record TrainingPlanRequest(@NotNull TrainingType trainingType,
                                  @NotNull PlanDuration planDuration,
                                  @Min(1) @Max(7) int daysPerWeek,
                                  @NotNull List<@NotNull WeekDay> preferredDays) {}
