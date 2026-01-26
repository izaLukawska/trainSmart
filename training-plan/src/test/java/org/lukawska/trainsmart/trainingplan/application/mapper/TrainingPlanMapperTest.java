package org.lukawska.trainsmart.trainingplan.application.mapper;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanDetails;
import org.lukawska.trainsmart.trainingplan.application.preparation.dto.TrainingPlanGenerationData;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingPlan;
import org.lukawska.trainsmart.trainingplan.model.SliceTrainingPlanSummaryResponse;
import org.lukawska.trainsmart.trainingplan.model.TrainingPlanResponse;
import org.lukawska.trainsmart.trainingplan.model.TrainingPlanSummaryResponse;
import org.lukawska.trainsmart.trainingplan.testutil.TrainingPlanTestData;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lukawska.trainsmart.trainingplan.testutil.TrainingPlanTestData.trainingPlan;
import static org.mockito.Mockito.mock;

class TrainingPlanMapperTest {

    @Test
    void shouldMapTrainingPlanToTrainingPlanDetails() {
        // given
        final TrainingPlan trainingPlan = trainingPlan(mock(User.class));

        // when
        TrainingPlanDetails details = TrainingPlanMapper.mapToTrainingPlanDetails(trainingPlan);

        // then
        assertThat(details.trainingType()).isEqualTo(trainingPlan.getTrainingType());
        assertThat(details.planDuration()).isEqualTo(trainingPlan.getPlanDuration());
        assertThat(details.trainingWeeks()).hasSize(trainingPlan.getWeeks().size());
    }

    @Test
    void shouldMapTrainingPlanToTrainingPlanSummary() {
        // given
        final TrainingPlan trainingPlan = trainingPlan(mock(User.class));

        // when
        TrainingPlanSummaryResponse result = TrainingPlanMapper.mapToTrainingPlanSummary(trainingPlan);

        // then
        assertThat(result.getPlanDuration().name()).isEqualTo(trainingPlan.getPlanDuration().name());
        assertThat(result.getTrainingType().name()).isEqualTo(trainingPlan.getTrainingType().name());
        assertThat(result.getDaysPerWeek()).isEqualTo(trainingPlan.getDaysPerWeek());
    }

    @Test
    void shouldMapSliceToSliceTrainingPlanSummaryResponse() {
        // given
        final TrainingPlan trainingPlan1 = trainingPlan(mock(User.class));
        final TrainingPlan trainingPlan2 = trainingPlan(mock(User.class));
        final Slice<TrainingPlan> slice = new SliceImpl<>(List.of(trainingPlan1, trainingPlan2));

        // when
        SliceTrainingPlanSummaryResponse result = TrainingPlanMapper.mapToTrainingPlanSummarySlice(slice);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getPageNumber()).isEqualTo(slice.getNumber());
        assertThat(result.getPageSize()).isEqualTo(slice.getSize());
        assertThat(result.getHasNext()).isEqualTo(slice.hasNext());
        assertThat(result.getIsFirst()).isEqualTo(slice.isFirst());
        assertThat(result.getLast()).isEqualTo(slice.isLast());
    }

    @Test
    void shouldMapTrainingPlanGenerationDataToTrainingPlan() {
        // given
        final TrainingPlanGenerationData generationData = TrainingPlanTestData.generationData();

        // when
        TrainingPlan result = TrainingPlanMapper.mapToTrainingPlan(generationData);

        // then
        assertThat(result.getTrainingType()).isEqualTo(generationData.trainingType());
        assertThat(result.getPlanDuration()).isEqualTo(generationData.planDuration());
        assertThat(result.getDaysPerWeek()).isEqualTo(generationData.preferredDays().size());
    }

    @Test
    void shouldMapTrainingPlanToTrainingPlanResponse() {
        // given
        final TrainingPlan trainingPlan = trainingPlan(mock(User.class));

        // when
        TrainingPlanResponse result = TrainingPlanMapper.mapToTrainingPlanResponse(trainingPlan);

        //then
        assertThat(result.getTrainingType().name()).isEqualTo(trainingPlan.getTrainingType().name());
        assertThat(result.getPlanDuration().name()).isEqualTo(trainingPlan.getPlanDuration().name());
        assertThat(result.getTrainingWeeks()).hasSize(trainingPlan.getWeeks().size());
    }
}
