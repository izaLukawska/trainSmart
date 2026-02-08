package org.lukawska.trainsmart.trainingplan.application.resolvers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanDto;
import org.lukawska.trainsmart.trainingplan.application.preparation.dto.TrainingPlanGenerationData;
import org.lukawska.trainsmart.trainingplan.application.preparation.resolvers.TrainingPlanDataResolver;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.WeekDay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import({TestFixtures.class, PostgresTestConfig.class})
@Transactional
class TrainingPlanDataResolverIT {

    @Autowired
    private TestFixtures testFixtures;

    @Autowired
    private TrainingPlanDataResolver trainingPlanDataResolver;

    private User user;

    @BeforeEach
    void setUp() {
        user = testFixtures.user().save();
        testFixtures.setUpUserExercises(user);
    }

    @Test
    void shouldReturnTrainingPlanGenerationDataWhenGetResolvedData() {
        //given
        final TrainingPlanDto trainingPlanDto = new TrainingPlanDto(TrainingType.STRENGTH, PlanDuration.EIGHT_WEEKS,
                                                                    2, List.of(WeekDay.MONDAY, WeekDay.FRIDAY));

        //when
        TrainingPlanGenerationData resolvedData = trainingPlanDataResolver.getResolvedData(user, trainingPlanDto,
                                                                                           Optional.empty());

        //then
        assertThat(resolvedData.preferredDays()).isEqualTo(trainingPlanDto.preferredDays());
        assertThat(resolvedData.planDuration()).isEqualTo(trainingPlanDto.planDuration());
        assertThat(resolvedData.trainingType()).isEqualTo(trainingPlanDto.trainingType());
    }
}
