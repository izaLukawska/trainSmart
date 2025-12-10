package org.lukawska.trainsmart.trainingplan.application.preparation.dto;

import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.WeekDay;

import java.util.List;
import java.util.Map;

public record TrainingPlanGenerationData(User user,
                                         Map<MuscleGroup, List<UserExercise>> muscleGroups,
                                         TrainingType trainingType,
                                         PlanDuration planDuration,
                                         List<WeekDay> preferredDays) {}
