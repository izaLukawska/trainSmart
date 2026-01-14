package org.lukawska.trainsmart.trainingplan.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;
import org.lukawska.trainsmart.trainingplan.model.SliceUserExerciseResponse;
import org.lukawska.trainsmart.trainingplan.model.UserExerciseResponse;
import org.springframework.data.domain.Slice;

import java.util.List;

@UtilityClass
public class UserExerciseMapper {

    public static UserExerciseResponse mapToResponse(UserExercise userExercise) {
        return new UserExerciseResponse(userExercise.getId(), userExercise.getExercise().getName(),
                                        userExercise.isEnabled(), userExercise.getLastUsedAt());
    }

    public static SliceUserExerciseResponse mapToUserExerciseResponseSlice(Slice<UserExercise> slice) {
        return SliceUserExerciseResponse.builder()
                                        .content(mapToUserExerciseResponseList(slice.getContent()))
                                        .pageNumber(slice.getNumber())
                                        .pageSize(slice.getSize())
                                        .hasNext(slice.hasNext())
                                        .isFirst(slice.isFirst())
                                        .last(slice.isLast())
                                        .build();
    }

    private List<UserExerciseResponse> mapToUserExerciseResponseList(List<UserExercise> userExerciseList) {
        return userExerciseList.stream()
                               .map(UserExerciseMapper::mapToResponse)
                               .toList();
    }
}
