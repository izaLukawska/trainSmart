package org.lukawska.trainsmart.healthsurvey.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyCreateRequest;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyResponse;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyUpdateRequest;
import org.lukawska.trainsmart.healthsurvey.application.exception.ExceptionType;
import org.lukawska.trainsmart.healthsurvey.application.exception.HealthSurveyException;
import org.lukawska.trainsmart.healthsurvey.domain.entites.HealthSurvey;
import org.lukawska.trainsmart.healthsurvey.domain.repositories.HealthSurveyRepository;
import org.lukawska.trainsmart.shared_persistence.application.exception.UserNotFoundException;
import org.lukawska.trainsmart.shared_persistence.application.service.UserService;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lukawska.trainsmart.healthsurvey.testutil.HealthSurveyTestData.healthSurveyEntity;
import static org.lukawska.trainsmart.healthsurvey.testutil.HealthSurveyTestData.healthSurveyRequest;
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
        final HealthSurveyCreateRequest createHealthSurveyRequest = healthSurveyRequest();
        when(userService.getUserById(2L)).thenReturn(mock(User.class));

        //when
        HealthSurveyResponse result = healthSurveyService.submitHealthSurvey(2L, createHealthSurveyRequest);

        //then
        verify(healthSurveyRepository, times(1)).save(any());
        assertThat(result.injuriesCount()).isEqualTo(3);
        assertThat(result.weight()).isEqualTo(60);
    }

    @Test
    void shouldUpdateHealthSurveySuccess() {
        //given
        final HealthSurveyUpdateRequest updateHealthSurveyRequest = new HealthSurveyUpdateRequest(70, List.of());
        final HealthSurvey existingHealthSurvey = healthSurveyEntity();
        when(healthSurveyRepository.findByUserId(1L)).thenReturn(Optional.of(existingHealthSurvey));

        //when
        HealthSurveyResponse result = healthSurveyService.updateHealthSurvey(1L, updateHealthSurveyRequest);

        //then
        assertThat(result.id()).isEqualTo(existingHealthSurvey.getId());
        assertThat(result.injuriesCount()).isEqualTo(0);
        assertThat(result.weight()).isEqualTo(70);
    }

    @Test
    void shouldSkipFieldsUpdateWhenValuesAreNull() {
        //given
        final HealthSurvey expectedHealthSurvey = healthSurveyEntity();
        when(healthSurveyRepository.findByUserId(1L)).thenReturn(Optional.of(expectedHealthSurvey));
        final HealthSurveyUpdateRequest updateHealthSurveyRequest = new HealthSurveyUpdateRequest(null, null);

        //when
        HealthSurveyResponse result = healthSurveyService.updateHealthSurvey(1L, updateHealthSurveyRequest);

        //then
        assertThat(result.id()).isEqualTo(expectedHealthSurvey.getId());
        assertThat(result.weight()).isEqualTo(expectedHealthSurvey.getWeight());
        assertThat(result.injuriesCount()).isEqualTo(expectedHealthSurvey.getInjuries().size());
    }

    @Test
    void shouldReturnHealthSurveyByUserIdResponse() {
        //given
        final HealthSurvey expectedHealthSurvey = healthSurveyEntity();
        when(healthSurveyRepository.findByUserId(1L)).thenReturn(Optional.of(expectedHealthSurvey));

        //when
        HealthSurveyResponse resultHealthSurvey = healthSurveyService.getHealthSurveyByUserIdResponse(1L);

        //then
        assertThat(resultHealthSurvey.id()).isEqualTo(expectedHealthSurvey.getId());
        assertThat(resultHealthSurvey.injuriesCount()).isEqualTo(expectedHealthSurvey.getInjuries().size());
        assertThat(resultHealthSurvey.weight()).isEqualTo(expectedHealthSurvey.getWeight());
        assertThat(resultHealthSurvey.gender()).isEqualTo(expectedHealthSurvey.getGender());
    }

    @Test
    void getAllInjuriesByUserId() {
        //given
        final HealthSurvey existingHealthSurvey = healthSurveyEntity();
        when(healthSurveyRepository.findByUserId(1L)).thenReturn(Optional.of(existingHealthSurvey));
        String[] expectedInjuries = existingHealthSurvey.getInjuries().toArray(String[]::new);

        //when
        List<String> actualInjuries = healthSurveyService.getAllInjuriesByUserId(1L);

        //then
        assertThat(actualInjuries).hasSize(3);
        assertThat(actualInjuries).containsExactlyInAnyOrder(expectedInjuries);
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

    @Test
    void shouldThrowUserNotFoundExceptionWhenSubmitHealthSurvey() {
        //given
        when(userService.getUserById(any())).thenThrow(new UserNotFoundException(2L));

        //when && then
        assertThatThrownBy(() -> healthSurveyService.submitHealthSurvey(2L, healthSurveyRequest()))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found for ID: %d", 2L);
    }

    @Test
    void shouldThrowHealthSurveyAlreadyExistsExceptionWhenSubmitHealthSurvey() {
        //given
        HealthSurveyCreateRequest createHealthSurveyRequest = healthSurveyRequest();
        when(healthSurveyRepository.existsByUserId(1L)).thenReturn(true);

        //when && then
        assertThatThrownBy(() -> healthSurveyService.submitHealthSurvey(1L, createHealthSurveyRequest))
                .isInstanceOf(HealthSurveyException.class)
                .hasMessage(ExceptionType.HEALTH_SURVEY_ALREADY_EXISTS.getMessage());
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
}
