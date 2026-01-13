package org.lukawska.trainsmart.exercisecatalog.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.exercisecatalog.application.dto.ExerciseRequest;
import org.lukawska.trainsmart.exercisecatalog.application.dto.ExerciseResponse;
import org.lukawska.trainsmart.exercisecatalog.domain.entities.Exercise;

@UtilityClass
public class ExerciseMapper {

    public static Exercise mapToExercise(ExerciseRequest exerciseRequest) {
        return new Exercise(exerciseRequest.name(), exerciseRequest.muscleGroup(), exerciseRequest.exerciseType());
    }

    public static ExerciseResponse mapToResponse(Exercise exercise) {
        return new ExerciseResponse(exercise.getId(), exercise.getName());
    }
}
