package org.lukawska.trainsmart.trainingplan.application.dto;

import java.util.List;

public record TrainingWeekResponse(Long weekId, int weekIndex, List<TrainingBlockResponse> trainingBlocks) {}
