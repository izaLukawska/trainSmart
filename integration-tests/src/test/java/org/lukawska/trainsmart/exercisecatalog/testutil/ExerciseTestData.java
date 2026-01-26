package org.lukawska.trainsmart.exercisecatalog.testutil;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.exercisecatalog.application.dto.ExerciseRequest;
import org.lukawska.trainsmart.exercisecatalog.application.dto.ExerciseResponse;
import org.lukawska.trainsmart.exercisecatalog.domain.entities.Exercise;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObjects.ExerciseType;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObjects.MuscleGroup;

import java.util.Random;
import java.util.UUID;

@UtilityClass
public class ExerciseTestData {

    public static ExerciseRequest randomQuadsDumbbellExerciseRequest() {
        return new ExerciseRequest(randomExerciseName(), MuscleGroup.QUADS, ExerciseType.DUMBBELL);
    }

    public static ExerciseResponse randomExerciseResponse() {
        return new ExerciseResponse(new Random().nextLong(), randomExerciseName());
    }

    public static Exercise randomQuadsDumbbellExercise() {
        return new Exercise(randomExerciseName(), MuscleGroup.QUADS, ExerciseType.DUMBBELL);
    }

    public static String randomExerciseName() {
        return UUID.randomUUID().toString();
    }
}
