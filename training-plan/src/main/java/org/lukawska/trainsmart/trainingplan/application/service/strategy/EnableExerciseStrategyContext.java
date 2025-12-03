package org.lukawska.trainsmart.trainingplan.application.service.strategy;

public record EnableExerciseStrategyContext(boolean injuriesEmpty, boolean injuriesUpdated, boolean hasNewExercises) {
}
