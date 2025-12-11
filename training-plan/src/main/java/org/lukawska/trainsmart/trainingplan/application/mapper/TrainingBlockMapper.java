package org.lukawska.trainsmart.trainingplan.application.mapper;

import org.lukawska.trainsmart.trainingplan.application.dto.TrainingBlockResponse;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingBlock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BlockExerciseMapper.class})
public interface TrainingBlockMapper {

    @Mapping(target = "blockId", source = "id")
    @Mapping(target = "assignedDay", source = "assignedDay")
    @Mapping(target = "blockExercises", source = "blockExercises")
    TrainingBlockResponse toResponse(TrainingBlock entity);

}
