package org.lukawska.trainsmart.trainingplan.application.dto.response;

import org.lukawska.trainsmart.trainingplan.domain.valueObjects.WeekDay;

import java.util.List;

public record TrainingBlockResponse(Long blockId, WeekDay assignedDay, List<BlockExerciseResponse> blockExercises) {}
