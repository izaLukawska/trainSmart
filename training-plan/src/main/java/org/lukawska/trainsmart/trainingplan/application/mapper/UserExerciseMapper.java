package org.lukawska.trainsmart.trainingplan.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.exercisecatalog.domain.entity.Exercise;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.application.dto.UserExerciseResponse;
import org.lukawska.trainsmart.trainingplan.domain.entity.UserExercise;

import java.util.List;

@UtilityClass
public class UserExerciseMapper {

    public static List<UserExercise> mapToUserExercises(List<Exercise> exercises, User user) {
        return exercises.stream()
                        .map(exercise -> new UserExercise(user, exercise))
                        .toList();
    }

    public static UserExerciseResponse mapToResponse(UserExercise userExercise) {
        return new UserExerciseResponse(userExercise.getId(), userExercise.getExercise().getName());
    }
}
