package org.lukawska.trainsmart.trainingplan.application.dto;

import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;

import java.util.List;

public record TrainingPlanDetails(TrainingType trainingType,
                                  PlanDuration planDuration,
                                  List<TrainingWeekDetails> trainingWeeks) {}
