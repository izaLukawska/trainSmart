package org.lukawska.trainsmart.trainingplan.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.lukawska.trainsmart.sharedpersistence.application.service.UserService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanDetails;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanDto;
import org.lukawska.trainsmart.trainingplan.application.exception.ExceptionType;
import org.lukawska.trainsmart.trainingplan.application.exception.TrainingPlanException;
import org.lukawska.trainsmart.trainingplan.application.generation.TrainingPlanGenerator;
import org.lukawska.trainsmart.trainingplan.application.preparation.dto.TrainingPlanGenerationData;
import org.lukawska.trainsmart.trainingplan.application.preparation.resolvers.TrainingPlanDataResolver;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingPlan;
import org.lukawska.trainsmart.trainingplan.domain.repositories.TrainingPlanRepository;
import org.lukawska.trainsmart.trainingplan.model.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
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

    private static final Long USER_ID = 2L;

    private static final Long PLAN_ID = 1L;

    @Test
    void shouldDeleteTrainingPlan() {
        // when
        trainingPlanService.deleteTrainingPlanByIdAndUserId(PLAN_ID, USER_ID);

        // then
        verify(trainingPlanRepository).deleteByIdAndUserId(PLAN_ID, USER_ID);
    }

    @Test
    void shouldCreateTrainingPlanSuccessfully() {
        //given
        final User user = mock(User.class);
        final TrainingPlanDto request = trainingPlanRequest();
        final TrainingPlan generatedPlan = trainingPlan(user);
        final TrainingPlanGenerationData generationData = mock(TrainingPlanGenerationData.class);

        when(userService.getUserById(USER_ID)).thenReturn(user);
        when(trainingPlanDataResolver.getResolvedData(user, request, Optional.empty())).thenReturn(generationData);
        when(trainingPlanGenerator.generateTrainingPlan(generationData)).thenReturn(generatedPlan);

        //when
        TrainingPlanDetails actualResult = trainingPlanService.createTrainingPlan(USER_ID, request);

        //then
        assertThat(actualResult.planDuration()).isEqualTo(generatedPlan.getPlanDuration());
        assertThat(actualResult.trainingType()).isEqualTo(generatedPlan.getTrainingType());
        assertThat(actualResult.trainingWeeks().size()).isEqualTo(generatedPlan.getWeeks().size());
    }

    @ParameterizedTest
    @CsvSource({
            "STRENGTH, EIGHT_WEEKS",
            "STRENGTH, ",
            ", EIGHT_WEEKS",
            ", "
    })
    @SuppressWarnings("unchecked")
    void shouldGetAllTrainingPlansSummaryByUserIdBasedOnFilter(TrainingTypeEnum type, PlanDurationEnum duration) {
        //given
        final TrainingPlanFilterRequest filterRequest = TrainingPlanFilterRequest.builder()
                                                                                 .trainingType(type)
                                                                                 .planDuration(duration)
                                                                                 .build();
        final PagingRequest pagingRequest = PagingRequest.builder().build();
        final TrainingPlan trainingPlan1 = trainingPlan(mock(User.class));
        final TrainingPlan trainingPlan2 = trainingPlan(mock(User.class));
        final List<TrainingPlan> trainingPlans = List.of(trainingPlan1, trainingPlan2);
        final Page<TrainingPlan> mockedPage = new PageImpl<>(trainingPlans);

        when(trainingPlanRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(mockedPage);

        //when
        SliceTrainingPlanSummaryResponse result = trainingPlanService.getAllTrainingPlansSummaryByUserId(
                USER_ID, pagingRequest, filterRequest);

        //then
        List<TrainingPlanSummaryResponse> content = result.getContent();
        assertThat(content.getFirst().getId()).isEqualTo(trainingPlan1.getId());
        assertThat(content.getLast().getId()).isEqualTo(trainingPlan2.getId());
    }

    @Test
    void shouldGetTrainingPlanResponseByPlanId() {
        // given
        final TrainingPlan trainingPlan = trainingPlan(mock(User.class));
        when(trainingPlanRepository.findByIdAndUserId(PLAN_ID, USER_ID)).thenReturn(Optional.of(trainingPlan));

        // when
        TrainingPlanResponse actualResult = trainingPlanService.getTrainingPlanResponseByIdAndUserId(PLAN_ID, USER_ID);

        // then
        assertThat(actualResult.getPlanDuration().name()).isEqualTo(trainingPlan.getPlanDuration().name());
        assertThat(actualResult.getTrainingType().name()).isEqualTo(trainingPlan.getTrainingType().name());
        assertThat(actualResult.getTrainingWeeks().size()).isEqualTo(trainingPlan.getWeeks().size());
    }

    @Test
    void shouldGetTrainingPlanDetailsByPlanId() {
        // given
        final TrainingPlan trainingPlan = trainingPlan(mock(User.class));
        when(trainingPlanRepository.findByIdAndUserId(PLAN_ID, USER_ID)).thenReturn(Optional.of(trainingPlan));

        // when
        TrainingPlanDetails actualResult = trainingPlanService.getTrainingPlanDetailsByIdAndUserId(PLAN_ID, USER_ID);

        // then
        assertThat(actualResult.planDuration()).isEqualTo(trainingPlan.getPlanDuration());
        assertThat(actualResult.trainingType()).isEqualTo(trainingPlan.getTrainingType());
        assertThat(actualResult.trainingWeeks().size()).isEqualTo(trainingPlan.getWeeks().size());
    }

    @Test
    void shouldThrowTrainingPlanGenerationErrorWhenCreateTraining() {
        // given
        final TrainingPlanDto request = trainingPlanRequest();
        when(trainingPlanRepository.save(any())).thenThrow(new DataIntegrityViolationException("Exception"));

        //when && then
        assertThatThrownBy(() -> trainingPlanService.createTrainingPlan(USER_ID, request))
                .isInstanceOf(TrainingPlanException.class)
                .hasMessage(ExceptionType.INVALID_TRAINING_PLAN_DATA.getMessage());
    }

    @Test
    void shouldThrowTrainingPlanNotFoundWhenGetTrainingPlanByIdAndUserId() {
        // given
        when(trainingPlanRepository.findByIdAndUserId(PLAN_ID, USER_ID)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> trainingPlanService.getTrainingPlanByIdAndUserId(PLAN_ID, USER_ID))
                .isInstanceOf(TrainingPlanException.class)
                .hasMessage(ExceptionType.TRAINING_PLAN_NOT_FOUND.getMessage());
    }
}
