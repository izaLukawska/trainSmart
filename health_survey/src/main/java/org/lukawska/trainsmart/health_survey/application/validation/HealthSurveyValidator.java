package org.lukawska.trainsmart.health_survey.application.validation;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.health_survey.application.dto.HealthSurveyRequest;
import org.lukawska.trainsmart.health_survey.application.exception.ExceptionType;
import org.lukawska.trainsmart.health_survey.application.exception.HealthSurveyException;
import org.lukawska.trainsmart.health_survey.domain.repositories.HealthSurveyRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@RequiredArgsConstructor
@Component
public class HealthSurveyValidator {

    private final HealthSurveyRepository healthSurveyRepository;

    public void validateHealthSurveyCreationRequest(HealthSurveyRequest surveyRequest) {
        validateRequestAge(surveyRequest.birthDate());
        validateHealthSurveyUniqueness(surveyRequest);
    }

    public void validateWeight(Integer newWeight) {
        if (newWeight == null || newWeight <= 0) {
            throw new HealthSurveyException(ExceptionType.INVALID_WEIGHT);
        }
    }

    private void validateHealthSurveyUniqueness(HealthSurveyRequest surveyRequest) {
        if (healthSurveyRepository.findByUserId(surveyRequest.userId()).isPresent()) {
            throw new HealthSurveyException(ExceptionType.HEALTH_SURVEY_ALREADY_EXISTS);
        }
    }

    private void validateRequestAge(LocalDate birthDate) {
        if (Period.between(birthDate, LocalDate.now()).getYears() < 18) {
            throw new HealthSurveyException(ExceptionType.INVALID_AGE);
        }
    }
}
