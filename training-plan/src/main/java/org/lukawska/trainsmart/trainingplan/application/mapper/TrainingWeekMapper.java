package org.lukawska.trainsmart.trainingplan.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingWeekDetails;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingWeek;
import org.lukawska.trainsmart.trainingplan.model.TrainingWeekResponse;

import java.util.List;

import static org.lukawska.trainsmart.trainingplan.application.mapper.TrainingBlockMapper.mapToTrainingBlockDetailsList;
import static org.lukawska.trainsmart.trainingplan.application.mapper.TrainingBlockMapper.mapToTrainingBlockResponseList;

@UtilityClass
class TrainingWeekMapper {

    static TrainingWeekDetails mapToTrainingWeekDetails(TrainingWeek trainingWeek) {
        return new TrainingWeekDetails(trainingWeek.getWeekIndex(),
                                       mapToTrainingBlockDetailsList(trainingWeek.getTrainingBlocks()));
    }

    static List<TrainingWeekDetails> mapToTrainingWeekDetailsList(List<TrainingWeek> trainingWeekList) {
        return trainingWeekList.stream()
                               .map(TrainingWeekMapper::mapToTrainingWeekDetails)
                               .toList();
    }

    static TrainingWeekResponse mapToTrainingWeekResponse(TrainingWeek trainingWeek) {
        return TrainingWeekResponse.builder()
                                   .weekId(trainingWeek.getId())
                                   .weekIndex(trainingWeek.getWeekIndex())
                                   .trainingBlocks(mapToTrainingBlockResponseList(trainingWeek.getTrainingBlocks()))
                                   .build();
    }

    static List<TrainingWeekResponse> mapToTrainingWeekResponse(List<TrainingWeek> list) {
        return list.stream()
                   .map(TrainingWeekMapper::mapToTrainingWeekResponse).toList();
    }
}
