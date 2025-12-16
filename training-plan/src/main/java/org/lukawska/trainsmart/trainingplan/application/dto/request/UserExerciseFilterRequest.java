package org.lukawska.trainsmart.trainingplan.application.dto.request;

import lombok.Builder;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.ExerciseType;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;

@Builder
public record UserExerciseFilterRequest(Boolean enabled,
                                        MuscleGroup muscleGroup,
                                        ExerciseType exerciseType,
                                        String keyword) {}
