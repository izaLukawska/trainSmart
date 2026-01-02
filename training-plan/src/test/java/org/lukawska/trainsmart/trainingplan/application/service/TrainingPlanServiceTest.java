package org.lukawska.trainsmart.trainingplan.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.lukawska.trainsmart.sharedpersistence.application.service.UserService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanDto;
import org.lukawska.trainsmart.trainingplan.application.dto.request.PagingRequest;
import org.lukawska.trainsmart.trainingplan.application.dto.request.TrainingPlanFilterRequest;
import org.lukawska.trainsmart.trainingplan.application.dto.response.TrainingPlanResponse;
import org.lukawska.trainsmart.trainingplan.application.dto.response.TrainingPlanSummaryResponse;
import org.lukawska.trainsmart.trainingplan.application.exception.ExceptionType;
import org.lukawska.trainsmart.trainingplan.application.exception.TrainingPlanException;
import org.lukawska.trainsmart.trainingplan.application.generation.TrainingPlanGenerator;
import org.lukawska.trainsmart.trainingplan.application.preparation.dto.TrainingPlanGenerationData;
import org.lukawska.trainsmart.trainingplan.application.preparation.resolvers.TrainingPlanDataResolver;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingPlan;
import org.lukawska.trainsmart.trainingplan.domain.repositories.TrainingPlanRepository;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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

    private static Stream<TrainingPlanFilterRequest> provideFilterRequests() {
        return Stream.of(new TrainingPlanFilterRequest(TrainingType.STRENGTH, PlanDuration.EIGHT_WEEKS),
                         new TrainingPlanFilterRequest(TrainingType.STRENGTH),
                         new TrainingPlanFilterRequest(PlanDuration.EIGHT_WEEKS),
                         new TrainingPlanFilterRequest());
    }

    private static final Long userId = 2L;

    @Test
    void shouldDeleteTrainingPlan() {
        // given
        final Long planId = 42L;

        // when
        trainingPlanService.deleteTrainingPlanByIdAndUserId(planId, userId);

        // then
        verify(trainingPlanRepository).deleteByIdAndUserId(planId, userId);
    }

    @Test
    void shouldCreateTrainingPlanSuccessfully() {
        //given
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
        assertThat(actualResult.trainingWeeks().size()).isEqualTo(generatedPlan.getWeeks().size());
    }

    @ParameterizedTest
    @MethodSource("provideFilterRequests")
    @SuppressWarnings("unchecked")
    void shouldGetAllTrainingPlansSummaryByUserIdBasedOnFilter(TrainingPlanFilterRequest filterRequest) {
        //given
        final PagingRequest pagingRequest = PagingRequest.builder().build();
        final TrainingPlan trainingPlan1 = trainingPlan(mock(User.class));
        final TrainingPlan trainingPlan2 = trainingPlan(mock(User.class));
        final List<TrainingPlan> trainingPlans = List.of(trainingPlan1, trainingPlan2);
        final Page<TrainingPlan> mockedPage = new PageImpl<>(trainingPlans);

        when(trainingPlanRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(mockedPage);

        //when
        Slice<TrainingPlanSummaryResponse> result = trainingPlanService.getAllTrainingPlansSummaryByUserId(
                userId, pagingRequest, filterRequest);

        //then
        List<TrainingPlanSummaryResponse> content = result.getContent();
        assertThat(content.getFirst().id()).isEqualTo(trainingPlan1.getId());
        assertThat(content.getLast().id()).isEqualTo(trainingPlan2.getId());
    }

    @Test
    void shouldThrowTrainingPlanGenerationErrorWhenCreateTraining() {
        // given
        final TrainingPlanDto request = trainingPlanRequest();
        when(trainingPlanRepository.save(any())).thenThrow(new DataIntegrityViolationException("Exception"));

        //when && then
        assertThatThrownBy(() -> trainingPlanService.createTrainingPlan(userId, request))
                .isInstanceOf(TrainingPlanException.class)
                .hasMessage(ExceptionType.INVALID_TRAINING_PLAN_DATA.getMessage());
    }

    @Test
    void shouldThrowTrainingPlanNotFoundWhenGetTrainingPlanByIdAndUserId() {
        // given
        final Long planId = 99L;
        when(trainingPlanRepository.findByIdAndUserId(planId, userId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> trainingPlanService.getTrainingPlanByIdAndUserId(planId, userId))
                .isInstanceOf(TrainingPlanException.class)
                .hasMessage(ExceptionType.TRAINING_PLAN_NOT_FOUND.getMessage());
    }

    @Test
    void shouldGetTrainingPlanResponseByPlanId() {
        // given
        final Long planId = 7L;
        final TrainingPlan trainingPlan = trainingPlan(mock(User.class));

        when(trainingPlanRepository.findByIdAndUserId(planId, userId)).thenReturn(Optional.of(trainingPlan));

        // when
        TrainingPlanResponse actualResult = trainingPlanService.getTrainingPlanResponseByIdAndUserId(planId, userId);

        // then
        assertThat(actualResult.planDuration()).isEqualTo(trainingPlan.getPlanDuration());
        assertThat(actualResult.trainingType()).isEqualTo(trainingPlan.getTrainingType());
        assertThat(actualResult.trainingWeeks().size()).isEqualTo(trainingPlan.getWeeks().size());
    }
}
