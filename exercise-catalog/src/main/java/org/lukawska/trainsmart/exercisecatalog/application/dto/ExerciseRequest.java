package org.lukawska.trainsmart.exercisecatalog.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObjects.ExerciseType;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObjects.MuscleGroup;

public record ExerciseRequest(@NotBlank String name,
                              @NotNull MuscleGroup muscleGroup,
                              @NotNull ExerciseType exerciseType) {}
