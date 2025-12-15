package org.lukawska.trainsmart.trainingplan.application.dto.response;

import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;

import java.util.List;

public record TrainingPlanResponse(Long planId,
                                   TrainingType trainingType,
                                   PlanDuration planDuration,
                                   List<TrainingWeekResponse> weeks) {}
