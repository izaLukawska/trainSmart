package org.lukawska.trainsmart.trainingplan.application.dto;

import lombok.Builder;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObjects.ExerciseType;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObjects.MuscleGroup;

@Builder
public record UserExerciseFilterDto(Boolean enabled, MuscleGroup muscleGroup, ExerciseType exerciseType) {}
