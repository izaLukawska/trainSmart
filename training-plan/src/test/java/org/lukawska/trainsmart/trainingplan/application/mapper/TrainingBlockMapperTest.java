package org.lukawska.trainsmart.trainingplan.application.mapper;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingBlockDetails;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingBlock;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingWeek;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.WeekDay;
import org.lukawska.trainsmart.trainingplan.model.TrainingBlockResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lukawska.trainsmart.trainingplan.application.mapper.MapperTestData.randomBlockExercise;
import static org.mockito.Mockito.mock;

class TrainingBlockMapperTest {

    @Test
    void shouldMapTrainingBlockToTrainingBlockDetails() {
        // given
        final TrainingBlock trainingBlock = trainingBlock();

        // when
        TrainingBlockDetails details = TrainingBlockMapper.mapToTrainingBlockDetails(trainingBlock);

        // then
        assertThat(details.assignedDay()).isEqualTo(trainingBlock.getAssignedDay());
        assertThat(details.blockExercises()).hasSize(1);
    }

    @Test
    void shouldMapTrainingBlockListToTrainingBlockDetailsList() {
        // given
        final TrainingBlock trainingBlock1 = trainingBlock();
        final TrainingBlock trainingBlock2 = trainingBlock();
        final List<TrainingBlock> blocks = List.of(trainingBlock1, trainingBlock2);

        // when
        List<TrainingBlockDetails> result = TrainingBlockMapper.mapToTrainingBlockDetailsList(blocks);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    void shouldMapTrainingBlockToTrainingBlockResponse() {
        // given
        final TrainingBlock trainingBlock = trainingBlock();

        // when
        TrainingBlockResponse response = TrainingBlockMapper.mapToTrainingBlockResponse(trainingBlock);

        // then
        assertThat(response.getAssignedDay().name()).isEqualTo(trainingBlock.getAssignedDay().name());
        assertThat(response.getBlockExercises()).hasSize(1);
    }

    @Test
    void shouldMapTrainingBlockListToTrainingBlockResponseList() {
        // given
        final TrainingBlock trainingBlock1 = trainingBlock();
        final TrainingBlock trainingBlock2 = trainingBlock();
        final List<TrainingBlock> list = List.of(trainingBlock1, trainingBlock2);

        // when
        List<TrainingBlockResponse> result = TrainingBlockMapper.mapToTrainingBlockResponseList(list);

        // then
        assertThat(result).hasSize(2);
    }

    private TrainingBlock trainingBlock() {
        TrainingBlock trainingBlock = new TrainingBlock(mock(TrainingWeek.class), WeekDay.TUESDAY);
        trainingBlock.addBlockExercise(randomBlockExercise());
        return trainingBlock;
    }
}
