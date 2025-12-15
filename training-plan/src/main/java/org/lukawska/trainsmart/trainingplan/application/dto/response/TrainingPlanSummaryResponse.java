package org.lukawska.trainsmart.trainingplan.application.dto.response;

import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;

import java.time.Instant;

public record TrainingPlanSummaryResponse(Long id,
                                          TrainingType trainingType,
                                          PlanDuration planDuration,
                                          int daysPerWeek,
                                          Instant createdAt) {}
