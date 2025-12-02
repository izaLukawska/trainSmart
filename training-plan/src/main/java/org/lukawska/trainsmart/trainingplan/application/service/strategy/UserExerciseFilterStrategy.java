package org.lukawska.trainsmart.trainingplan.application.service.strategy;

import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.lukawska.trainsmart.trainingplan.application.service.UserExerciseContext;
import org.lukawska.trainsmart.trainingplan.domain.entity.UserExercise;

import java.util.List;
import java.util.Map;

public interface UserExerciseFilterStrategy {

    boolean matches(UserExerciseContext context);

    Map<MuscleGroup, List<UserExercise>> filterExercises(Long userId, UserExerciseContext context);

}
