package org.lukawska.trainsmart.exercisecatalog.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.exercisecatalog.application.dto.ExerciseRequest;
import org.lukawska.trainsmart.exercisecatalog.application.dto.ExerciseResponse;
import org.lukawska.trainsmart.exercisecatalog.domain.entity.Exercise;

@UtilityClass
public class ExerciseMapper {

    public static Exercise mapToExercise(ExerciseRequest exerciseRequest) {
        String exerciseName = normalizeExerciseName(exerciseRequest.name());
        return new Exercise(exerciseName, exerciseRequest.muscleGroup(), exerciseRequest.exerciseType());
    }

    public static ExerciseResponse mapToResponse(Exercise exercise) {
        return new ExerciseResponse(exercise.getId(), exercise.getName());
    }

    private String normalizeExerciseName(String name) {
        return name.replaceAll("[^A-Za-z]+", " ")
                   .replaceAll("\\s+", " ")
                   .trim()
                   .toLowerCase();
    }
}
