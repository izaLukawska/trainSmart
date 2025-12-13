package org.lukawska.trainsmart.trainingplan.application.generation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.trainingplan.application.preparation.dto.TrainingPlanGenerationData;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingPlan;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lukawska.trainsmart.trainingplan.testutil.TrainingPlanTestData.generationData;

@ExtendWith(MockitoExtension.class)
class TrainingPlanGeneratorTest {

    private final TrainingPlanGenerator trainingPlanGenerator = new TrainingPlanGenerator();

    @Test
    void shouldGenerateTrainingPlanSuccess() {
        //given
        final TrainingPlanGenerationData data = generationData();

        //when
        TrainingPlan result = trainingPlanGenerator.generateTrainingPlan(data);

        //then
        assertThat(result.getPlanDuration()).isEqualTo(data.planDuration());
        assertThat(result.getTrainingType()).isEqualTo(data.trainingType());
        assertThat(result.getDaysPerWeek()).isEqualTo(data.preferredDays().size());
        assertThat(result.getUser()).isEqualTo(data.user());
    }
}
