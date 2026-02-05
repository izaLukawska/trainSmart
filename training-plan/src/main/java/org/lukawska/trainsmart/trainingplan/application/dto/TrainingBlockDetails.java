package org.lukawska.trainsmart.trainingplan.application.dto;

import org.lukawska.trainsmart.trainingplan.domain.valueObjects.WeekDay;

import java.util.List;

public record TrainingBlockDetails(WeekDay assignedDay, List<BlockExerciseDetails> blockExercises) {}
