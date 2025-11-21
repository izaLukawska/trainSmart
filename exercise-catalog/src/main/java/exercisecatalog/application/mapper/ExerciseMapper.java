package exercisecatalog.application.mapper;

import exercisecatalog.application.dto.ExerciseRequest;
import exercisecatalog.application.dto.ExerciseResponse;
import exercisecatalog.domain.entity.Exercise;
import lombok.experimental.UtilityClass;

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
