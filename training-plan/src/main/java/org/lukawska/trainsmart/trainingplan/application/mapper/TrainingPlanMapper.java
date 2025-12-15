package org.lukawska.trainsmart.trainingplan.application.mapper;

import org.lukawska.trainsmart.trainingplan.application.dto.response.TrainingPlanResponse;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingPlan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = TrainingWeekMapper.class)
public interface TrainingPlanMapper {

    @Mapping(target = "planId", source = "id")
    @Mapping(target = "trainingType", source = "trainingType")
    @Mapping(target = "planDuration", source = "planDuration")
    @Mapping(target = "weeks", source = "weeks")
    TrainingPlanResponse toResponse(TrainingPlan entity);

}
