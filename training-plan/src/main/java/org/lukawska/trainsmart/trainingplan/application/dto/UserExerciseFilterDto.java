package org.lukawska.trainsmart.trainingplan.application.dto;

import lombok.Builder;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.ExerciseType;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;

@Builder
public record UserExerciseFilterDto(Boolean enabled, MuscleGroup muscleGroup, ExerciseType exerciseType) {}
