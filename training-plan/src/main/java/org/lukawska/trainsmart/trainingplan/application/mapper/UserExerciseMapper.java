package org.lukawska.trainsmart.trainingplan.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.trainingplan.application.dto.response.UserExerciseResponse;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;

import java.util.List;

@UtilityClass
public class UserExerciseMapper {

    public static UserExerciseResponse mapToResponse(UserExercise userExercise) {
        return new UserExerciseResponse(userExercise.getId(), userExercise.getExercise().getName());
    }

    public static List<UserExerciseResponse> mapToResponseList(List<UserExercise> userExerciseList) {
        return userExerciseList.stream()
                               .map(UserExerciseMapper::mapToResponse)
                               .toList();
    }
}
