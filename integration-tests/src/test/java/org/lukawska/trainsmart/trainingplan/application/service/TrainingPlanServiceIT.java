package org.lukawska.trainsmart.trainingplan.application.service;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanDto;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingPlan;
import org.lukawska.trainsmart.trainingplan.domain.repositories.TrainingPlanRepository;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.WeekDay;
import org.lukawska.trainsmart.trainingplan.model.*;
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
class TrainingPlanServiceIT {

    @Autowired
    private TrainingPlanRepository trainingPlanRepository;

    @Autowired
    private TestFixtures testFixtures;

    @Autowired
    private TrainingPlanService trainingPlanService;

    @Test
    void shouldCreateTrainingPlanForUser() {
        //given
        final User user = testFixtures.user().save();
        final Long userId = user.getId();
        testFixtures.setUpUserExercises(user);
        final TrainingPlanDto trainingPlanDto = new TrainingPlanDto(TrainingType.STRENGTH, PlanDuration.EIGHT_WEEKS,
                                                                    2, List.of(WeekDay.MONDAY, WeekDay.FRIDAY));

        //when
        TrainingPlan trainingPlan = trainingPlanService.createTrainingPlan(userId, trainingPlanDto);

        //then
        assertThat(trainingPlan.getPlanDuration()).isEqualTo(trainingPlanDto.planDuration());
        assertThat(trainingPlan.getTrainingType()).isEqualTo(trainingPlanDto.trainingType());
        assertThat(trainingPlan.getWeeks().size()).isEqualTo(trainingPlanDto.planDuration().getWeeksCount());
    }

    @Test
    void shouldDeleteTrainingPlanByIdAndUserIdSuccess() {
        //given
        final TrainingPlan trainingPlan = testFixtures.trainingPlan().save();
        final Long userId = trainingPlan.getUser().getId();
        final Long planId = trainingPlan.getId();

        //when
        trainingPlanService.deleteTrainingPlanByIdAndUserId(planId, userId);

        //then
        Optional<TrainingPlan> result = trainingPlanRepository.findById(planId);
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnTrainingPlanWhenGetTrainingPlanByIdAndUserId() {
        //given
        final TrainingPlan trainingPlan = testFixtures.trainingPlan().save();
        final Long userId = trainingPlan.getUser().getId();
        final Long planId = trainingPlan.getId();

        //when
        TrainingPlan result = trainingPlanService.getTrainingPlanByIdAndUserId(planId, userId);

        //then
        assertThat(result.getUser()).isEqualTo(trainingPlan.getUser());
        assertThat(result.getTrainingType()).isEqualTo(trainingPlan.getTrainingType());
        assertThat(result.getCreatedAt()).isEqualTo(trainingPlan.getCreatedAt());
        assertThat(result.getPlanDuration()).isEqualTo(trainingPlan.getPlanDuration());
    }

    @Test
    void shouldReturnSliceTrainingPlanSummaryResponseWhenGetAllTrainingPlansSummaryByUserId() {
        //given
        final TrainingPlan trainingPlan = testFixtures.trainingPlan().save();
        final Long userId = trainingPlan.getUser().getId();
        final PagingRequest pagingRequest = PagingRequest.builder()
                                                         .pageSize(1)
                                                         .pageNumber(0)
                                                         .direction(PagingRequest.DirectionEnum.ASC)
                                                         .sortBy(PagingRequest.SortByEnum.ID)
                                                         .build();
        final TrainingPlanFilterRequest filterRequest =
                TrainingPlanFilterRequest.builder()
                                         .trainingType(TrainingTypeEnum.STRENGTH)
                                         .build();

        //when
        SliceTrainingPlanSummaryResponse result = trainingPlanService.getAllTrainingPlansSummaryByUserId(
                userId, pagingRequest, filterRequest);

        //then
        TrainingPlanSummaryResponse trainingPlanSummaryResponse1 = result.getContent().getLast();
        assertThat(trainingPlanSummaryResponse1.getId()).isEqualTo(trainingPlan.getId());
        assertThat(result.getContent().size()).isEqualTo(1);
        assertThat(result.getIsFirst()).isTrue();
        assertThat(result.getLast()).isTrue();
        assertThat(result.getPageNumber()).isEqualTo(pagingRequest.getPageNumber());
        assertThat(result.getPageSize()).isEqualTo(pagingRequest.getPageSize());
    }

    @Test
    void shouldReturnTrainingPlanResponseWhenGetTrainingPlanResponseByIdAndUserId() {
        //given
        final TrainingPlan trainingPlan = testFixtures.trainingPlan().save();
        final Long planId = trainingPlan.getId();
        final Long userId = trainingPlan.getUser().getId();

        //when
        TrainingPlanResponse result = trainingPlanService.getTrainingPlanResponseByIdAndUserId(planId, userId);

        //then
        assertThat(result.getPlanId()).isEqualTo(trainingPlan.getId());
    }
}
