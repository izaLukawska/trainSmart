package org.lukawska.trainsmart.trainingplan.application.mapper;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingBlockResponse;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingBlock;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingWeek;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.WeekDay;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class TrainingBlockMapperTest {

    private final TrainingBlockMapper trainingBlockMapper = Mappers.getMapper(TrainingBlockMapper.class);

    @Test
    void shouldMapTrainingBlockToResponse() {
        //given
        final TrainingBlock trainingBlock = new TrainingBlock(mock(TrainingWeek.class), WeekDay.FRIDAY);

        //when
        TrainingBlockResponse result = trainingBlockMapper.toResponse(trainingBlock);

        //then
        assertThat(result.assignedDay()).isEqualTo(trainingBlock.getAssignedDay());
        assertThat(result.blockId()).isEqualTo(trainingBlock.getId());
        assertThat(result.blockExercises().size()).isEqualTo(trainingBlock.getBlockExercises().size());
    }
}
