package org.lukawska.trainsmart.trainingplan.testutil;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.exercisecatalog.domain.entity.Exercise;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;

import static org.mockito.Mockito.mock;

@UtilityClass
public class UserExerciseTestData {

    public static UserExercise userExerciseWithMockedData() {
        return new UserExercise(mock(User.class), mock(Exercise.class));
    }
}
