package org.lukawska.trainsmart.health_survey.application.service;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.health_survey.application.dto.HealthSurveyRequest;
import org.lukawska.trainsmart.health_survey.application.dto.HealthSurveyResponse;
import org.lukawska.trainsmart.health_survey.application.exception.ExceptionType;
import org.lukawska.trainsmart.health_survey.application.exception.HealthSurveyException;
import org.lukawska.trainsmart.health_survey.application.validation.HealthSurveyValidator;
import org.lukawska.trainsmart.health_survey.domain.entites.HealthSurvey;
import org.lukawska.trainsmart.health_survey.domain.repositories.HealthSurveyRepository;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;
import org.lukawska.trainsmart.shared_persistence.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;

import static org.lukawska.trainsmart.health_survey.application.mapper.HealthSurveyMapper.mapToEntity;
import static org.lukawska.trainsmart.health_survey.application.mapper.HealthSurveyMapper.mapToHealthSurveyResponse;

@Service
@RequiredArgsConstructor
public class HealthSurveyService {

    private final HealthSurveyRepository healthSurveyRepository;

    private final UserRepository userRepository;

    private final HealthSurveyValidator healthSurveyValidator;

    public HealthSurveyResponse fillHealthSurvey(HealthSurveyRequest surveyRequest) {
        healthSurveyValidator.validateHealthSurveyCreationRequest(surveyRequest);

        User user = userRepository.findById(surveyRequest.userId())
                                  .orElseThrow(() -> new HealthSurveyException(ExceptionType.USER_NOT_FOUND));

        HealthSurvey healthSurvey = mapToEntity(surveyRequest, user);
        healthSurveyRepository.save(healthSurvey);

        return mapToHealthSurveyResponse(healthSurvey);
    }

    public HealthSurveyResponse getHealthSurveyByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new HealthSurveyException(ExceptionType.USER_NOT_FOUND);
        }

        HealthSurvey existingHeathSurvey = healthSurveyRepository
                .findByUserId(userId)
                .orElseThrow(() -> new HealthSurveyException(ExceptionType.HEALTH_SURVEY_NOT_FOUND));

        return mapToHealthSurveyResponse(existingHeathSurvey);
    }

    public HealthSurveyResponse updateWeight(Long userId, Integer newWeight) {
        healthSurveyValidator.validateWeight(newWeight);

        HealthSurvey healthSurvey = healthSurveyRepository.findByUserId(userId)
                                                          .orElseThrow(() -> new HealthSurveyException(
                                                                  ExceptionType.HEALTH_SURVEY_NOT_FOUND));

        healthSurvey.updateWeight(newWeight);
        healthSurveyRepository.save(healthSurvey);

        return mapToHealthSurveyResponse(healthSurvey);
    }
}
