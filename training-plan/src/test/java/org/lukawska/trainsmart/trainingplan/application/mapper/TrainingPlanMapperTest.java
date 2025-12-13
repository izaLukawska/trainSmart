package org.lukawska.trainsmart.trainingplan.application.mapper;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanResponse;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingPlan;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingWeek;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lukawska.trainsmart.trainingplan.testutil.TrainingPlanTestData.trainingPlan;
import static org.mockito.Mockito.mock;

class TrainingPlanMapperTest {

    private final TrainingPlanMapper trainingPlanMapper = Mappers.getMapper(TrainingPlanMapper.class);

    @Test
    void shouldMapTrainingPlanToResponse() {
        // given
        final TrainingPlan plan = trainingPlan(mock(User.class));
        plan.addTrainingWeek(mock(TrainingWeek.class));

        // when
        TrainingPlanResponse response = trainingPlanMapper.toResponse(plan);

        // then
        assertThat(response).isNotNull();
        assertThat(response.planId()).isEqualTo(plan.getId());
        assertThat(response.trainingType()).isEqualTo(plan.getTrainingType());
        assertThat(response.planDuration()).isEqualTo(plan.getPlanDuration());
        assertThat(response.weeks().size()).isEqualTo(1);
    }
}
