package org.lukawska.trainsmart.healthsurvey.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyRequest;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyResponse;
import org.lukawska.trainsmart.healthsurvey.application.exception.ExceptionType;
import org.lukawska.trainsmart.healthsurvey.application.exception.HealthSurveyException;
import org.lukawska.trainsmart.healthsurvey.domain.entites.HealthSurvey;
import org.lukawska.trainsmart.healthsurvey.domain.repositories.HealthSurveyRepository;
import org.lukawska.trainsmart.healthsurvey.domain.valueObjects.Gender;
import org.lukawska.trainsmart.shared_persistence.application.service.UserService;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lukawska.trainsmart.healthsurvey.application.testutil.HealthSurveyTestData.healthSurveyEntity;
import static org.lukawska.trainsmart.healthsurvey.application.testutil.HealthSurveyTestData.healthSurveyRequest;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealthSurveyServiceTest {

    @Mock
    private HealthSurveyRepository healthSurveyRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private HealthSurveyService healthSurveyService;

    @Test
    void shouldSubmitHealthSurveySuccess() {
        //given
        final HealthSurveyRequest healthSurveyRequest = healthSurveyRequest();
        when(userService.getUserById(healthSurveyRequest.userId())).thenReturn(mock(User.class));
        final int expectedAge = Period.between(healthSurveyRequest.birthDate(), LocalDate.now()).getYears();
        //when
        HealthSurveyResponse healthSurveyResponse = healthSurveyService.submitHealthSurvey(healthSurveyRequest);

        //then
        verify(healthSurveyRepository, times(1)).save(any());
        assertThat(healthSurveyResponse.injuriesCount()).isEqualTo(3);
        assertThat(healthSurveyResponse.weight()).isEqualTo(60);
        assertThat(healthSurveyResponse.age()).isEqualTo(expectedAge);
    }

    @Test
    void shouldThrowHealthSurveyAlreadyExistsExceptionWhenSubmitHealthSurvey() {
        //given
        HealthSurveyRequest healthSurveyRequest = healthSurveyRequest();
        when(healthSurveyRepository.existsByUserId(healthSurveyRequest.userId())).thenReturn(true);

        //when && then
        assertThatThrownBy(() -> healthSurveyService.submitHealthSurvey(healthSurveyRequest))
                .isInstanceOf(HealthSurveyException.class)
                .hasMessage(ExceptionType.HEALTH_SURVEY_ALREADY_EXISTS.getMessage());
    }

    @Test
    void shouldReturnHealthSurveyByUserIdResponse() {
        //given
        when(healthSurveyRepository.findByUserId(1L)).thenReturn(Optional.of(healthSurveyEntity()));

        //when
        HealthSurveyResponse response = healthSurveyService.getHealthSurveyByUserIdResponse(1L);

        //then
        assertThat(response.injuriesCount()).isEqualTo(3);
        assertThat(response.weight()).isEqualTo(80);
        assertThat(response.gender()).isEqualTo(Gender.MALE);
    }

    @Test
    void shouldThrowHealthSurveyNotFoundExceptionWhenGetHealthSurveyByUserId() {
        //given
        when(healthSurveyRepository.findByUserId(1L)).thenReturn(Optional.empty());

        //when && then
        assertThatThrownBy(() -> healthSurveyService.getHealthSurveyByUserIdResponse(1L))
                .isInstanceOf(HealthSurveyException.class)
                .hasMessage(ExceptionType.HEALTH_SURVEY_NOT_FOUND.getMessage());
    }

    @Test
    void getAllInjuriesByUserId() {
        //given
        when(healthSurveyRepository.findByUserId(1L)).thenReturn(Optional.of(healthSurveyEntity()));

        //when
        List<String> actualInjuries = healthSurveyService.getAllInjuriesByUserId(1L);

        //then
        assertThat(actualInjuries).hasSize(3);
        assertThat(actualInjuries).containsExactlyInAnyOrder("wrist pain", "dislocated finger", "scoliosis");
    }

    @Test
    void updateInjuries() {
        //given
        when(healthSurveyRepository.findByUserId(1L)).thenReturn(Optional.of(healthSurveyEntity()));

        //when
        HealthSurveyResponse response = healthSurveyService.updateInjuries(1L, List.of("swollen eye", "scoliosis"));

        //then
        assertThat(response.injuriesCount()).isEqualTo(2);
    }

    @Test
    void updateWeight() {
        //given
        when(healthSurveyRepository.findByUserId(1L)).thenReturn(Optional.of(healthSurveyEntity()));

        //when
        HealthSurveyResponse response = healthSurveyService.updateWeight(1L, 70);

        //then
        assertThat(response.weight()).isEqualTo(70);
    }

    @Test
    void deleteHealthSurveyByUserId() {
        //given
        final HealthSurvey healthSurvey = mock(HealthSurvey.class);
        when(healthSurveyRepository.findByUserId(1L)).thenReturn(Optional.of(healthSurvey));

        //when
        healthSurveyService.deleteHealthSurveyByUserId(1L);

        //then
        verify(healthSurveyRepository).delete(healthSurvey);
    }
}
