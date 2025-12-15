package org.lukawska.trainsmart.trainingplan.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.sharedpersistence.application.service.UserService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanDto;
import org.lukawska.trainsmart.trainingplan.application.dto.response.TrainingPlanResponse;
import org.lukawska.trainsmart.trainingplan.application.exception.ExceptionType;
import org.lukawska.trainsmart.trainingplan.application.exception.TrainingPlanException;
import org.lukawska.trainsmart.trainingplan.application.generation.TrainingPlanGenerator;
import org.lukawska.trainsmart.trainingplan.application.preparation.dto.TrainingPlanGenerationData;
import org.lukawska.trainsmart.trainingplan.application.preparation.resolvers.TrainingPlanDataResolver;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingPlan;
import org.lukawska.trainsmart.trainingplan.domain.repositories.TrainingPlanRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lukawska.trainsmart.trainingplan.testutil.TrainingPlanTestData.trainingPlan;
import static org.lukawska.trainsmart.trainingplan.testutil.TrainingPlanTestData.trainingPlanRequest;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingPlanServiceTest {

    @Mock
    private TrainingPlanRepository trainingPlanRepository;

    @Mock
    private UserService userService;

    @Mock
    private TrainingPlanGenerator trainingPlanGenerator;

    @Mock
    private TrainingPlanDataResolver trainingPlanDataResolver;

    @InjectMocks
    private TrainingPlanService trainingPlanService;

    @Test
    void shouldCreateTrainingPlanSuccessfully() {
        //given
        final Long userId = 1L;
        final User user = mock(User.class);
        final TrainingPlanDto request = trainingPlanRequest();
        final TrainingPlan generatedPlan = trainingPlan(user);
        final TrainingPlanGenerationData generationData = mock(TrainingPlanGenerationData.class);

        when(userService.getUserById(userId)).thenReturn(user);
        when(trainingPlanDataResolver.getResolvedData(user, request, Optional.empty())).thenReturn(generationData);
        when(trainingPlanGenerator.generateTrainingPlan(generationData)).thenReturn(generatedPlan);

        //when
        TrainingPlanResponse actualResult = trainingPlanService.createTrainingPlan(userId, request);

        //then
        assertThat(actualResult.planDuration()).isEqualTo(generatedPlan.getPlanDuration());
        assertThat(actualResult.trainingType()).isEqualTo(generatedPlan.getTrainingType());
        assertThat(actualResult.weeks().size()).isEqualTo(generatedPlan.getWeeks().size());
    }

    @Test
    void shouldDeleteTrainingPlan() {
        // given
        final Long planId = 42L;
        final Long userId = 2L;

        // when
        trainingPlanService.deleteTrainingPlanByIdAndUserId(planId, userId);

        // then
        verify(trainingPlanRepository).deleteByIdAndUserId(planId, userId);
    }

    @Test
    void shouldGetTrainingPlanResponseByPlanId() {
        // given
        final Long planId = 7L;
        final Long userId = 2L;
        final TrainingPlan trainingPlan = trainingPlan(mock(User.class));

        when(trainingPlanRepository.findByIdAndUserId(planId, userId)).thenReturn(Optional.of(trainingPlan));

        // when
        TrainingPlanResponse actualResult = trainingPlanService.getTrainingPlanResponseByIdAndUserId(planId, userId);

        // then
        assertThat(actualResult.planDuration()).isEqualTo(trainingPlan.getPlanDuration());
        assertThat(actualResult.trainingType()).isEqualTo(trainingPlan.getTrainingType());
        assertThat(actualResult.weeks().size()).isEqualTo(trainingPlan.getWeeks().size());
    }

    @Test
    void shouldThrowWhenPlanNotFound() {
        // given
        final Long planId = 99L;
        final Long userId = 2L;
        when(trainingPlanRepository.findByIdAndUserId(planId, userId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> trainingPlanService.getTrainingPlanByIdAndUserId(planId, userId))
                .isInstanceOf(TrainingPlanException.class)
                .hasMessage(ExceptionType.TRAINING_PLAN_NOT_FOUND.getMessage());
    }
}
