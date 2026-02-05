package org.lukawska.trainsmart.healthsurvey.application.service;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyCreateRequest;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyResponse;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyUpdateRequest;
import org.lukawska.trainsmart.healthsurvey.application.dto.WeightHistoryResponse;
import org.lukawska.trainsmart.healthsurvey.application.exception.ExceptionType;
import org.lukawska.trainsmart.healthsurvey.application.exception.HealthSurveyException;
import org.lukawska.trainsmart.healthsurvey.domain.entites.HealthSurvey;
import org.lukawska.trainsmart.healthsurvey.domain.repositories.HealthSurveyRepository;
import org.lukawska.trainsmart.healthsurvey.domain.valueObjects.Gender;
import org.lukawska.trainsmart.sharedpersistence.application.exception.UserNotFoundException;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Import({PostgresTestConfig.class, TestFixtures.class})
@Transactional
class HealthSurveyServiceIT {

    @Autowired
    private HealthSurveyRepository healthSurveyRepository;

    @Autowired
    private HealthSurveyService healthSurveyService;

    @Autowired
    private TestFixtures testFixtures;

    @Test
    void shouldSaveAndReturnHealthSurveyResponseWhenSubmitHealthSurvey() {
        //given
        final User user = testFixtures.user().save();
        final Long userId = user.getId();
        final HealthSurveyCreateRequest request = healthSurveyRequest();

        //when
        HealthSurveyResponse result = healthSurveyService.submitHealthSurvey(userId, request);

        //then
        assertThat(result.injuriesCount()).isEqualTo(request.injuries().size());
        assertThat(result.gender()).isEqualTo(request.gender());
        assertThat(result.weight()).isEqualTo(request.weight());
        assertThat(result.height()).isEqualTo(request.height());
    }

    @Test
    void shouldReturnHealthSurveyResponseWhenUpdateHealth() {
        //given
        final HealthSurvey healthSurvey = testFixtures.healthSurvey().save();
        final Long userId = healthSurvey.getUser().getId();
        final Integer updatedWeight = 30;
        final Set<String> updatedInjuries = new HashSet<>(Set.of("ankle"));
        healthSurveyRepository.save(healthSurvey);
        HealthSurveyUpdateRequest updateRequest = new HealthSurveyUpdateRequest(updatedWeight, updatedInjuries);

        //when
        HealthSurveyResponse result = healthSurveyService.updateHealthSurvey(userId, updateRequest);

        //then
        assertThat(result.id()).isEqualTo(healthSurvey.getId());
        assertThat(result.weight()).isEqualTo(updatedWeight);
        assertThat(result.injuriesCount()).isEqualTo(updatedInjuries.size());
    }

    @Test
    void shouldDeleteHealthSurveyByUserId() {
        //given
        final HealthSurvey healthSurvey = testFixtures.healthSurvey().save();
        final Long userId = healthSurvey.getUser().getId();

        //when
        healthSurveyService.deleteHealthSurveyByUserId(userId);

        //then
        assertThat(healthSurveyRepository.findByUserId(userId)).isEmpty();
    }

    @Test
    void shouldReturnHealthSurveyByUserIdResponse() {
        //given
        final HealthSurvey healthSurvey = testFixtures.healthSurvey().save();
        final Long userId = healthSurvey.getUser().getId();

        //when
        HealthSurveyResponse result = healthSurveyService.getHealthSurveyByUserIdResponse(userId);

        //then
        assertThat(result.injuriesCount()).isEqualTo(healthSurvey.getInjuries().size());
        assertThat(result.gender()).isEqualTo(healthSurvey.getGender());
        assertThat(result.weight()).isEqualTo(healthSurvey.getWeight());
        assertThat(result.height()).isEqualTo(healthSurvey.getHeight());
    }

    @Test
    void shouldReturnInjuriesWhenGetAllInjuriesByUserId() {
        //given
        final HealthSurvey healthSurvey = testFixtures.healthSurvey().save();
        final Long userId = healthSurvey.getUser().getId();

        //when
        Set<String> actualResult = healthSurveyService.getAllInjuriesByUserId(userId);

        //then
        assertThat(actualResult).isEqualTo(healthSurvey.getInjuries());
    }

    @Test
    void shouldReturnWeightHistoryResponseListWhenWeightHistoryByUserId() {
        //given
        final User user = testFixtures.user()
                                      .withUsername("user" + new Random().nextInt())
                                      .save();
        final HealthSurvey healthSurvey = testFixtures.healthSurvey()
                                                      .forUser(user)
                                                      .build();
        final Long userId = healthSurvey.getUser().getId();
        final Integer initialWeight = healthSurvey.getWeight();
        final Integer updatedWeight = 60;
        healthSurveyRepository.saveAndFlush(healthSurvey);
        TestTransaction.flagForCommit();
        TestTransaction.end();

        TestTransaction.start();
        healthSurvey.updateWeight(updatedWeight);
        healthSurveyRepository.saveAndFlush(healthSurvey);
        TestTransaction.flagForCommit();
        TestTransaction.end();

        //when
        List<WeightHistoryResponse> result = healthSurveyService.getWeightHistoryByUserId(userId);

        //then
        assertThat(result).hasSize(2);
        assertThat(result.getFirst().weight()).isEqualTo(initialWeight);
        assertThat(result.getLast().weight()).isEqualTo(updatedWeight);
    }

    @Test
    void shouldReturnHealthSurveyEntityByUserId() {
        //given
        final HealthSurvey healthSurvey = testFixtures.healthSurvey().save();
        final Long userId = healthSurvey.getUser().getId();

        //when
        HealthSurvey result = healthSurveyService.getExistingHealthSurvey(userId);

        //then
        assertThat(result.getId()).isEqualTo(healthSurvey.getId());
        assertThat(result.getUser()).isEqualTo(healthSurvey.getUser());
        assertThat(result.getInjuries()).isEqualTo(healthSurvey.getInjuries());
        assertThat(result.getWeight()).isEqualTo(healthSurvey.getWeight());
        assertThat(result.getHeight()).isEqualTo(healthSurvey.getHeight());
        assertThat(result.getInjuriesUpdatedAt()).isEqualTo(healthSurvey.getInjuriesUpdatedAt());
        assertThat(result.getGender()).isEqualTo(healthSurvey.getGender());
    }

    @Test
    void shouldThrowHealthSurveyNotFoundExceptionWhenGetHealthSurveyByUserId() {
        //given
        final User user = testFixtures.user().save();
        final Long userId = user.getId();

        //when && then
        assertThatThrownBy(() -> healthSurveyService.getExistingHealthSurvey(userId))
                .isInstanceOf(HealthSurveyException.class)
                .hasMessage(ExceptionType.HEALTH_SURVEY_NOT_FOUND.getMessage());
    }

    @Test
    void shouldThrowHealthSurveyNotFoundExceptionWhenDeleteHealthSurveyByUserId() {
        //given
        final User user = testFixtures.user().save();
        final Long userId = user.getId();

        //when && then
        assertThatThrownBy(() -> healthSurveyService.deleteHealthSurveyByUserId(userId))
                .isInstanceOf(HealthSurveyException.class)
                .hasMessage(ExceptionType.HEALTH_SURVEY_NOT_FOUND.getMessage());
    }

    @Test
    void shouldShouldThrowHealthSurveyAlreadyExistsExceptionWhenSubmitHealthSurvey() {
        //given
        final User user = testFixtures.user().save();
        final HealthSurveyCreateRequest request = healthSurveyRequest();
        final Long userId = user.getId();
        testFixtures.healthSurvey()
                    .forUser(user)
                    .save();

        //when && then
        assertThatThrownBy(() -> healthSurveyService.submitHealthSurvey(userId, request))
                .isInstanceOf(HealthSurveyException.class)
                .hasMessage(ExceptionType.HEALTH_SURVEY_ALREADY_EXISTS.getMessage());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenSubmitHealthSurvey() {
        //given
        final HealthSurveyCreateRequest request = healthSurveyRequest();
        final Long userId = 2L;

        //when && then
        assertThatThrownBy(() -> healthSurveyService.submitHealthSurvey(userId, request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage(new UserNotFoundException().getMessage());
    }

    private HealthSurveyCreateRequest healthSurveyRequest() {
        return new HealthSurveyCreateRequest(Gender.FEMALE, 180, 80, Set.of());
    }
}
