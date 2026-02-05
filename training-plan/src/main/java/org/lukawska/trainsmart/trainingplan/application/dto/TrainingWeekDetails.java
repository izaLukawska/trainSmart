package org.lukawska.trainsmart.trainingplan.application.dto;

import java.util.List;

public record TrainingWeekDetails(int weekIndex, List<TrainingBlockDetails> trainingBlocks) {}
