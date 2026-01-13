package org.lukawska.trainsmart.trainingplan.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;
import org.lukawska.trainsmart.trainingplan.model.SliceUserExerciseResponse;
import org.lukawska.trainsmart.trainingplan.model.UserExerciseResponse;
import org.springframework.data.domain.Page;

@UtilityClass
public class UserExerciseMapper {

    public static UserExerciseResponse mapToResponse(UserExercise userExercise) {
        return new UserExerciseResponse(userExercise.getId(), userExercise.getExercise().getName(),
                                        userExercise.isEnabled(), userExercise.getLastUsedAt());
    }

    public static SliceUserExerciseResponse mapToUserExerciseResponseSlice(Page<UserExercise> page) {
        return new SliceUserExerciseResponse()
                .content(page.getContent()
                             .stream()
                             .map(UserExerciseMapper::mapToResponse)
                             .toList())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements((int) page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast());
    }
}
