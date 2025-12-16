package org.lukawska.trainsmart.trainingplan.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.trainingplan.application.dto.response.TrainingBlockResponse;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingBlock;

import java.util.List;

import static org.lukawska.trainsmart.trainingplan.application.mapper.BlockExerciseMapper.mapToBlockExerciseResponseList;

@UtilityClass
class TrainingBlockMapper {

    static TrainingBlockResponse mapToTrainingBlockResponse(TrainingBlock trainingBlock) {
        return new TrainingBlockResponse(trainingBlock.getId(), trainingBlock.getAssignedDay(),
                                         mapToBlockExerciseResponseList(trainingBlock.getBlockExercises()));

    }

    static List<TrainingBlockResponse> mapToTrainingBlockResponseList(List<TrainingBlock> trainingBlockList) {
        return trainingBlockList.stream()
                                .map(TrainingBlockMapper::mapToTrainingBlockResponse)
                                .toList();
    }
}
