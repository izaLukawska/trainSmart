package org.lukawska.trainsmart.trainingplan.application.mapper;

import org.lukawska.trainsmart.trainingplan.application.dto.UserExerciseResponse;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserExerciseMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "exerciseName", source = "exercise.name")
    UserExerciseResponse toResponse(UserExercise userExercise);

    List<UserExerciseResponse> toResponseList(List<UserExercise> userExercises);

    List<String> toExerciseNames(List<UserExercise> userExercises);

    default String toExerciseName(UserExercise userExercise) {
        return userExercise.getExercise().getName();
    }
}
