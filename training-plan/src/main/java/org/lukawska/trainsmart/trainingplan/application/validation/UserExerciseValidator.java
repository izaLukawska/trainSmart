package org.lukawska.trainsmart.trainingplan.application.validation;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.lukawska.trainsmart.trainingplan.application.exception.ExceptionType;
import org.lukawska.trainsmart.trainingplan.application.exception.TrainingPlanException;
import org.lukawska.trainsmart.trainingplan.domain.entity.UserExercise;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserExerciseValidator {

    public void validateUserExercises(Map<MuscleGroup, List<UserExercise>> userExercisesMuscleGroups) {
        validateMuscleGroupSize(userExercisesMuscleGroups);
        validateUserExerciseSize(userExercisesMuscleGroups);
    }

    private void validateMuscleGroupSize(Map<MuscleGroup, List<UserExercise>> userExercisesMuscleGroups) {
        if (userExercisesMuscleGroups.size() < 3) {
            throw new TrainingPlanException(ExceptionType.NOT_ENOUGH_MUSCLE_GROUPS);
        }
    }

    private void validateUserExerciseSize(Map<MuscleGroup, List<UserExercise>> userExercisesMuscleGroups) {
        if (userExercisesMuscleGroups.values().stream().anyMatch(List::isEmpty)) {
            throw new TrainingPlanException(ExceptionType.NOT_ENOUGH_EXERCISES);
        }
    }
}
