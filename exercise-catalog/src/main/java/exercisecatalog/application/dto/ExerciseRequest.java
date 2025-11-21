package exercisecatalog.application.dto;

import exercisecatalog.domain.valueObject.ExerciseType;
import exercisecatalog.domain.valueObject.MuscleGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExerciseRequest(@NotBlank String name,
                              @NotNull MuscleGroup muscleGroup,
                              @NotNull ExerciseType exerciseType) {}
