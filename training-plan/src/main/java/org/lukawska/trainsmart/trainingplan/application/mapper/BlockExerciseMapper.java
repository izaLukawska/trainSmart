package org.lukawska.trainsmart.trainingplan.application.mapper;

import org.lukawska.trainsmart.trainingplan.application.dto.BlockExerciseResponse;
import org.lukawska.trainsmart.trainingplan.domain.entities.BlockExercise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BlockExerciseMapper {

    @Mapping(target = "exerciseName", source = "userExercise.exercise.name")
    @Mapping(target = "reps", source = "reps")
    @Mapping(target = "sets", source = "sets")
    @Mapping(target = "intensity", source = "intensity")
    @Mapping(target = "loadPercent", source = "loadPercent")
    BlockExerciseResponse toResponse(BlockExercise entity);

}
