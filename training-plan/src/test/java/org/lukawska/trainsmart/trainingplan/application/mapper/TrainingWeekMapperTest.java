package org.lukawska.trainsmart.trainingplan.application.mapper;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingWeekDetails;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingBlock;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingPlan;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingWeek;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.WeekDay;
import org.lukawska.trainsmart.trainingplan.model.TrainingWeekResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TrainingWeekMapperTest {

    @Test
    void shouldMapTrainingWeekToTrainingWeekDetails() {
        // given
        final TrainingWeek trainingWeek = trainingWeekFixture();

        // when
        TrainingWeekDetails result = TrainingWeekMapper.mapToTrainingWeekDetails(trainingWeek);

        // then
        assertThat(result.weekIndex()).isEqualTo(trainingWeek.getWeekIndex());
        assertThat(result.trainingBlocks()).hasSize(trainingWeek.getTrainingBlocks().size());
    }

    @Test
    void shouldMapTrainingWeekListToTrainingWeekDetailsList() {
        // given
        final TrainingWeek trainingWeek1 = trainingWeekFixture();
        final TrainingWeek trainingWeek2 = trainingWeekFixture();
        trainingWeek2.addTrainingBlock(mock(TrainingBlock.class));

        final List<TrainingWeek> weeks = List.of(trainingWeek1, trainingWeek2);

        // when
        List<TrainingWeekDetails> result = TrainingWeekMapper.mapToTrainingWeekDetailsList(weeks);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).trainingBlocks()).hasSize(1);
        assertThat(result.get(1).trainingBlocks()).hasSize(2);
    }

    @Test
    void shouldMapTrainingWeekToTrainingWeekResponse() {
        // given
        final TrainingWeek trainingWeek = trainingWeekFixture();

        // when
        TrainingWeekResponse result = TrainingWeekMapper.mapToTrainingWeekResponse(trainingWeek);

        // then
        assertThat(result.getTrainingBlocks()).hasSize(trainingWeek.getTrainingBlocks().size());
    }

    @Test
    void shouldMapTrainingWeekListToTrainingWeekResponseList() {
        // given
        final TrainingWeek trainingWeek1 = trainingWeekFixture();
        final TrainingWeek trainingWeek2 = trainingWeekFixture();
        final TrainingBlock trainingBlock = mock(TrainingBlock.class);
        final List<TrainingWeek> weeks = List.of(trainingWeek1, trainingWeek2);
        trainingWeek2.addTrainingBlock(trainingBlock);
        when(trainingBlock.getAssignedDay()).thenReturn(WeekDay.TUESDAY);

        // when
        List<TrainingWeekResponse> result = TrainingWeekMapper.mapToTrainingWeekResponse(weeks);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.getFirst().getTrainingBlocks()).hasSize(trainingWeek1.getTrainingBlocks().size());
        assertThat(result.getLast().getTrainingBlocks()).hasSize(trainingWeek2.getTrainingBlocks().size());
    }

    private TrainingWeek trainingWeekFixture() {
        TrainingWeek trainingWeek = new TrainingWeek(mock(TrainingPlan.class), 2);
        TrainingBlock trainingBlock = mock(TrainingBlock.class);
        trainingWeek.addTrainingBlock(trainingBlock);
        when(trainingBlock.getAssignedDay()).thenReturn(WeekDay.MONDAY);
        return trainingWeek;
    }
}
