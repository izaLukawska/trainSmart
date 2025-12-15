package org.lukawska.trainsmart.trainingplan.application.mapper.training;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.trainingplan.application.dto.response.TrainingWeekResponse;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingWeek;

import java.util.List;

import static org.lukawska.trainsmart.trainingplan.application.mapper.training.TrainingBlockMapper.mapToTrainingBlockResponseList;

@UtilityClass
class TrainingWeekMapper {

    static TrainingWeekResponse mapToTrainingWeekResponse(TrainingWeek trainingWeek) {
        return new TrainingWeekResponse(trainingWeek.getId(), trainingWeek.getWeekIndex(),
                                        mapToTrainingBlockResponseList(trainingWeek.getTrainingBlocks()));
    }

    static List<TrainingWeekResponse> mapToTrainingWeekResponseList(List<TrainingWeek> trainingWeekList) {
        return trainingWeekList.stream()
                               .map(TrainingWeekMapper::mapToTrainingWeekResponse)
                               .toList();
    }
}
