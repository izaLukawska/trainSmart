package org.lukawska.trainsmart.trainingplan.application.mapper.training;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.trainingplan.application.dto.response.BlockExerciseResponse;
import org.lukawska.trainsmart.trainingplan.domain.entities.BlockExercise;

import java.util.List;

@UtilityClass
class BlockExerciseMapper {

    static BlockExerciseResponse mapToBlockExerciseResponse(BlockExercise blockExercise) {
        return new BlockExerciseResponse(blockExercise.getUserExercise().getExercise().getName(),
                                         blockExercise.getReps(),
                                         blockExercise.getSets(),
                                         blockExercise.getIntensity(),
                                         blockExercise.getLoadPercent());
    }

    static List<BlockExerciseResponse> mapToBlockExerciseResponseList(List<BlockExercise> blockExerciseList) {
        return blockExerciseList.stream()
                                .map(BlockExerciseMapper::mapToBlockExerciseResponse)
                                .toList();
    }
}
