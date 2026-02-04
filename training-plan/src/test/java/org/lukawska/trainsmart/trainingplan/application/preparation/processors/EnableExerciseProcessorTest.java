package org.lukawska.trainsmart.trainingplan.application.preparation.processors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObjects.MuscleGroup;
import org.lukawska.trainsmart.healthsurvey.application.service.HealthSurveyService;
import org.lukawska.trainsmart.healthsurvey.domain.entites.HealthSurvey;
import org.lukawska.trainsmart.trainingplan.application.preparation.resolvers.EnableExerciseStrategyResolver;
import org.lukawska.trainsmart.trainingplan.application.preparation.strategies.EnableExerciseStrategy;
import org.lukawska.trainsmart.trainingplan.application.service.UserExerciseService;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lukawska.trainsmart.trainingplan.testutil.TrainingPlanTestData.validMuscleGroups;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnableExerciseProcessorTest {

    @Mock
    private HealthSurveyService healthSurveyService;

    @Mock
    private UserExerciseService userExerciseService;

    @Mock
    private EnableExerciseStrategyResolver strategyResolver;

    @InjectMocks
    private EnableExerciseProcessor enableExerciseProcessor;

    private static final Long userId = 1L;

    @Test
    void shouldReturnUserExercisesByMuscleGroupWhenStrategyNotPresent() {
        //given
        final Map<MuscleGroup, List<UserExercise>> expectedResult = validMuscleGroups();

        when(healthSurveyService.getExistingHealthSurvey(userId)).thenReturn(mock(HealthSurvey.class));
        when(userExerciseService.getEnabledUserExercisesByMuscleGroup(userId)).thenReturn(expectedResult);
        when(strategyResolver.chooseStrategy(any())).thenReturn(Optional.empty());

        //when
        Map<MuscleGroup, List<UserExercise>> actualResult = enableExerciseProcessor.enableUserExercises(
                userId, Optional.empty());

        //then
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void shouldReturnUserExercisesByMuscleGroupWhenStrategyPresent() {
        //given
        final Optional<Instant> lastUpdateDate = Optional.of(Instant.now());
        final Map<MuscleGroup, List<UserExercise>> expectedResult = validMuscleGroups();
        HealthSurvey healthSurvey = mock(HealthSurvey.class);

        when(healthSurveyService.getExistingHealthSurvey(userId)).thenReturn(healthSurvey);
        when(healthSurvey.getInjuriesUpdatedAt()).thenReturn(lastUpdateDate.get());
        when(userExerciseService.getEnabledUserExercisesByMuscleGroup(userId)).thenReturn(expectedResult);
        when(strategyResolver.chooseStrategy(any())).thenReturn(Optional.of(mock(EnableExerciseStrategy.class)));

        //when
        Map<MuscleGroup, List<UserExercise>> actualResult = enableExerciseProcessor.enableUserExercises(
                userId, lastUpdateDate);

        //then
        assertThat(actualResult).isEqualTo(expectedResult);
    }
}
