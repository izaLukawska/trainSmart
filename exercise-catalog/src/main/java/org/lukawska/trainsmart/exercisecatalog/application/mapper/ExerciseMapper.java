package org.lukawska.trainsmart.exercisecatalog.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.exercisecatalog.domain.entities.Exercise;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObjects.ExerciseType;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObjects.MuscleGroup;
import org.lukawska.trainsmart.exercisecatalog.model.ExerciseRequest;
import org.lukawska.trainsmart.exercisecatalog.model.ExerciseResponse;

@UtilityClass
public class ExerciseMapper {

    public static Exercise mapToExercise(ExerciseRequest exerciseRequest) {
        return new Exercise(exerciseRequest.getName(), MuscleGroup.valueOf(exerciseRequest.getMuscleGroup().name()),
                            ExerciseType.valueOf(exerciseRequest.getExerciseType().name()));
    }

    public static ExerciseResponse mapToResponse(Exercise exercise) {
        return new ExerciseResponse(exercise.getId(), exercise.getName());
    }
}
