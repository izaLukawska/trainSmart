package org.lukawska.trainsmart.trainingplan.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.trainingplan.application.dto.BlockExerciseDetails;
import org.lukawska.trainsmart.trainingplan.domain.entities.BlockExercise;
import org.lukawska.trainsmart.trainingplan.model.BlockExerciseResponse;
import org.lukawska.trainsmart.trainingplan.model.IntensityLevelEnum;

import java.util.List;

@UtilityClass
class BlockExerciseMapper {

    static BlockExerciseDetails mapToBlockExerciseDetails(BlockExercise blockExercise) {
        return new BlockExerciseDetails(blockExercise.getUserExercise().getExercise().getName(),
                                        blockExercise.getReps(),
                                        blockExercise.getSets(),
                                        blockExercise.getIntensity(),
                                        blockExercise.getLoadPercent());
    }

    static List<BlockExerciseDetails> mapToBlockExerciseDetailsList(List<BlockExercise> blockExerciseList) {
        return blockExerciseList.stream()
                                .map(BlockExerciseMapper::mapToBlockExerciseDetails)
                                .toList();
    }

    static BlockExerciseResponse mapToBlockExerciseResponse(BlockExercise blockExercise) {
        return BlockExerciseResponse.builder()
                                    .exerciseName(blockExercise.getUserExercise().getExercise().getName())
                                    .reps(blockExercise.getReps())
                                    .sets(blockExercise.getSets())
                                    .intensityLevel(IntensityLevelEnum.valueOf(blockExercise.getIntensity().name()))
                                    .loadPercent(blockExercise.getLoadPercent()).build();
    }

    static List<BlockExerciseResponse> mapToBlockExerciseResposnseList(List<BlockExercise> blockExerciseList) {
        return blockExerciseList.stream()
                                .map(BlockExerciseMapper::mapToBlockExerciseResponse)
                                .toList();
    }
}
