package org.lukawska.trainsmart.trainingplan.application.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import org.lukawska.trainsmart.trainingplan.application.serializer.LoadPercentageSerializer;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.IntensityLevel;

@Builder
public record BlockExerciseDetails(String exerciseName,
                                   int reps,
                                   int sets,
                                   IntensityLevel intensity,
                                   @JsonSerialize(using = LoadPercentageSerializer.class)
                                   Double loadPercent) {}
