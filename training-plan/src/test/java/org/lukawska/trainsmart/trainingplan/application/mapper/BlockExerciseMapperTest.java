package org.lukawska.trainsmart.trainingplan.application.mapper;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.trainingplan.application.dto.BlockExerciseDetails;
import org.lukawska.trainsmart.trainingplan.domain.entities.BlockExercise;
import org.lukawska.trainsmart.trainingplan.model.BlockExerciseResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lukawska.trainsmart.trainingplan.application.mapper.MapperTestData.randomBlockExercise;

class BlockExerciseMapperTest {

    @Test
    void shouldMapToBlockExerciseDetails() {
        //given=
        final BlockExercise block = randomBlockExercise();

        //when
        BlockExerciseDetails result = BlockExerciseMapper.mapToBlockExerciseDetails(block);

        //then
        assertThat(result.exerciseName()).isEqualTo(block.getUserExercise().getExercise().getName());
        assertThat(result.reps()).isEqualTo(block.getReps());
        assertThat(result.sets()).isEqualTo(block.getSets());
        assertThat(result.intensity().name()).isEqualTo(block.getIntensity().name());
        assertThat(result.loadPercent()).isEqualTo(block.getLoadPercent());
    }

    @Test
    void shouldMapToBlockExerciseDetailsList() {
        //given
        final BlockExercise block1 = randomBlockExercise();
        final BlockExercise block2 = randomBlockExercise();
        final List<BlockExercise> list = List.of(block1, block2);

        //when
        List<BlockExerciseDetails> result = BlockExerciseMapper.mapToBlockExerciseDetailsList(list);

        //then
        assertThat(result).hasSize(2);
        BlockExerciseDetails blockExerciseDetails1 = result.getFirst();
        assertThat(blockExerciseDetails1.exerciseName()).isEqualTo(block1.getUserExercise().getExercise().getName());
        assertThat(blockExerciseDetails1.intensity().name()).isEqualTo(block1.getIntensity().name());

        BlockExerciseDetails blockExerciseDetails2 = result.getLast();
        assertThat(blockExerciseDetails2.exerciseName()).isEqualTo(block2.getUserExercise().getExercise().getName());
        assertThat(blockExerciseDetails2.loadPercent()).isEqualTo(block2.getLoadPercent());
    }

    @Test
    void shouldMapToBlockExerciseResponse() {
        //given
        final BlockExercise block = randomBlockExercise();

        //when
        BlockExerciseResponse result = BlockExerciseMapper.mapToBlockExerciseResponse(block);

        //then
        assertThat(result.getExerciseName()).isEqualTo(block.getUserExercise().getExercise().getName());
        assertThat(result.getReps()).isEqualTo(block.getReps());
        assertThat(result.getSets()).isEqualTo(block.getSets());
        assertThat(result.getIntensityLevel().name()).isEqualTo(block.getIntensity().name());
        assertThat(result.getLoadPercent()).isEqualTo(block.getLoadPercent());
    }

    @Test
    void shouldMapToBlockExerciseResponseList() {
        //given
        final BlockExercise block1 = randomBlockExercise();
        final BlockExercise block2 = randomBlockExercise();
        final List<BlockExercise> list = List.of(block1, block2);

        //when
        List<BlockExerciseResponse> result = BlockExerciseMapper.mapToBlockExerciseResponseList(list);
        assertThat(result).hasSize(2);

        BlockExerciseResponse response1 = result.getFirst();
        assertThat(response1.getExerciseName()).isEqualTo(block1.getUserExercise().getExercise().getName());
        assertThat(response1.getIntensityLevel().name()).isEqualTo(block1.getIntensity().name());

        BlockExerciseResponse response2 = result.getLast();
        assertThat(response2.getExerciseName()).isEqualTo(block2.getUserExercise().getExercise().getName());
        assertThat(response2.getIntensityLevel().name()).isEqualTo(block2.getIntensity().name());
    }
}
