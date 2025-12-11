package org.lukawska.trainsmart.trainingplan.application.dto;

import org.lukawska.trainsmart.trainingplan.domain.valueObjects.WeekDay;

import java.util.List;

public record TrainingBlockResponse(Long blockId, WeekDay assignedDay, List<BlockExerciseResponse> blockExercises) {}
