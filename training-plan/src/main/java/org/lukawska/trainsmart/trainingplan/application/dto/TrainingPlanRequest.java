package org.lukawska.trainsmart.trainingplan.application.dto;

import org.lukawska.trainsmart.trainingplan.domain.valueObjects.Duration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;

public record TrainingPlanRequest(Long userId, TrainingType trainingType, Duration duration, int daysPerWeek) {}
