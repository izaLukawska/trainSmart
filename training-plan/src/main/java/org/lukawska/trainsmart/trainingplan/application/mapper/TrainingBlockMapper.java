package org.lukawska.trainsmart.trainingplan.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingBlockDetails;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingBlock;
import org.lukawska.trainsmart.trainingplan.model.AssignedDayEnum;
import org.lukawska.trainsmart.trainingplan.model.TrainingBlockResponse;

import java.util.List;

import static org.lukawska.trainsmart.trainingplan.application.mapper.BlockExerciseMapper.mapToBlockExerciseDetailsList;
import static org.lukawska.trainsmart.trainingplan.application.mapper.BlockExerciseMapper.mapToBlockExerciseResponseList;

@UtilityClass
class TrainingBlockMapper {

    static TrainingBlockDetails mapToTrainingBlockDetails(TrainingBlock trainingBlock) {
        return new TrainingBlockDetails(trainingBlock.getAssignedDay(),
                                        mapToBlockExerciseDetailsList(trainingBlock.getBlockExercises()));
    }

    static List<TrainingBlockDetails> mapToTrainingBlockDetailsList(List<TrainingBlock> trainingBlockList) {
        return trainingBlockList.stream()
                                .map(TrainingBlockMapper::mapToTrainingBlockDetails)
                                .toList();
    }

    static TrainingBlockResponse mapToTrainingBlockResponse(TrainingBlock trainingBlock) {
        return TrainingBlockResponse.builder()
                                    .blockId(trainingBlock.getId())
                                    .assignedDay(AssignedDayEnum.valueOf(trainingBlock.getAssignedDay().name()))
                                    .blockExercises(mapToBlockExerciseResponseList(trainingBlock.getBlockExercises()))
                                    .build();
    }

    static List<TrainingBlockResponse> mapToTrainingBlockResponseList(List<TrainingBlock> list) {
        return list.stream()
                   .map(TrainingBlockMapper::mapToTrainingBlockResponse)
                   .toList();
    }
}
