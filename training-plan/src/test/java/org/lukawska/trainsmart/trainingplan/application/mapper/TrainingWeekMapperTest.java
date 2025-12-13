package org.lukawska.trainsmart.trainingplan.application.mapper;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingWeekResponse;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingPlan;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingWeek;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class TrainingWeekMapperTest {

    private final TrainingWeekMapper trainingWeekMapper = Mappers.getMapper(TrainingWeekMapper.class);

    @Test
    void shouldMapTrainingWeekToResponse() {
        //given
        final TrainingWeek trainingWeek = new TrainingWeek(mock(TrainingPlan.class), 2);

        //when
        TrainingWeekResponse result = trainingWeekMapper.toResponse(trainingWeek);

        //then
        assertThat(result.weekIndex()).isEqualTo(trainingWeek.getWeekIndex());
        assertThat(result.weekId()).isEqualTo(trainingWeek.getId());
        assertThat(result.trainingBlocks().size()).isEqualTo(trainingWeek.getTrainingBlocks().size());
    }

    @Test
    void shouldMapListOfTrainingWeekToResponseList() {
        //given
        final TrainingWeek trainingWeek1 = new TrainingWeek(mock(TrainingPlan.class), 2);
        final TrainingWeek trainingWeek2 = new TrainingWeek(mock(TrainingPlan.class), 3);
        final List<TrainingWeek> trainingWeeks = List.of(trainingWeek1, trainingWeek2);

        //when
        List<TrainingWeekResponse> result = trainingWeekMapper.toDtoList(trainingWeeks);

        //then
        TrainingWeekResponse resultWeek1 = result.getFirst();
        TrainingWeekResponse resultWeek2 = result.getLast();

        assertThat(resultWeek1.weekId()).isEqualTo(trainingWeek1.getId());
        assertThat(resultWeek1.weekIndex()).isEqualTo(trainingWeek1.getWeekIndex());
        assertThat(resultWeek1.trainingBlocks().size()).isEqualTo(trainingWeek1.getTrainingBlocks().size());

        assertThat(resultWeek2.weekId()).isEqualTo(trainingWeek2.getId());
        assertThat(resultWeek2.weekIndex()).isEqualTo(trainingWeek2.getWeekIndex());
        assertThat(resultWeek2.trainingBlocks().size()).isEqualTo(trainingWeek2.getTrainingBlocks().size());
    }

}
