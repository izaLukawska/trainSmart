package org.lukawska.trainsmart.trainingplan.application.mapper;

import org.lukawska.trainsmart.trainingplan.application.dto.TrainingWeekResponse;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingWeek;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = TrainingBlockMapper.class)
public interface TrainingWeekMapper {

    @Mapping(target = "weekId", source = "id")
    @Mapping(target = "weekIndex", source = "weekIndex")
    @Mapping(target = "trainingBlocks", source = "trainingBlocks")
    TrainingWeekResponse toResponse(TrainingWeek entity);

    List<TrainingWeekResponse> toDtoList(List<TrainingWeek> trainingWeeks);

}
