package org.lukawska.trainsmart.trainingplan.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.exercisecatalog.domain.entities.Exercise;
import org.lukawska.trainsmart.trainingplan.domain.entities.BlockExercise;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.IntensityLevel;

import java.util.Random;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UtilityClass
public class MapperTestData {

    public static BlockExercise randomBlockExercise() {
        UserExercise userExercise = mock(UserExercise.class);
        Exercise exercise = mock(Exercise.class);
        when(userExercise.getExercise()).thenReturn(exercise);
        when(exercise.getName()).thenReturn(UUID.randomUUID().toString());

        return BlockExercise.builder()
                            .userExercise(userExercise)
                            .reps(new Random().nextInt())
                            .sets(new Random().nextInt())
                            .intensity(IntensityLevel.MEDIUM)
                            .loadPercent(new Random().nextDouble()).build();
    }
}
