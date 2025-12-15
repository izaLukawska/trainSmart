package org.lukawska.trainsmart.trainingplan.application.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.lukawska.trainsmart.trainingplan.application.serializer.LoadPercentageSerializer;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.IntensityLevel;

public record BlockExerciseResponse(String exerciseName,
                                    int reps,
                                    int sets,
                                    IntensityLevel intensity,
                                    @JsonSerialize(using = LoadPercentageSerializer.class)
                                    Double loadPercent) {}
