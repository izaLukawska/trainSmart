package org.lukawska.trainsmart.health_survey.application.service;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.health_survey.application.dto.HealthSurveyRequest;
import org.lukawska.trainsmart.health_survey.application.dto.HealthSurveyResponse;
import org.lukawska.trainsmart.health_survey.application.exception.ExceptionType;
import org.lukawska.trainsmart.health_survey.application.exception.HealthSurveyException;
import org.lukawska.trainsmart.health_survey.domain.entites.HealthSurvey;
import org.lukawska.trainsmart.health_survey.domain.repositories.HealthSurveyRepository;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;
import org.lukawska.trainsmart.shared_persistence.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static org.lukawska.trainsmart.health_survey.application.mapper.HealthSurveyMapper.mapToEntity;
import static org.lukawska.trainsmart.health_survey.application.mapper.HealthSurveyMapper.mapToHealthSurveyResponse;

@Service
@RequiredArgsConstructor
public class HealthSurveyService {

    private final HealthSurveyRepository healthSurveyRepository;

    private final UserRepository userRepository;

    public HealthSurveyResponse submitHealthSurvey(HealthSurveyRequest surveyRequest) {
        if (healthSurveyRepository.findByUserId(surveyRequest.userId()).isPresent()) {
            throw new HealthSurveyException(ExceptionType.HEALTH_SURVEY_ALREADY_EXISTS);
        }

        User user = userRepository.findById(surveyRequest.userId())
                                  .orElseThrow(() -> new HealthSurveyException(ExceptionType.USER_NOT_FOUND));


        HealthSurvey healthSurvey = mapToEntity(surveyRequest, user);
        healthSurveyRepository.save(healthSurvey);

        return mapToHealthSurveyResponse(healthSurvey);
    }

    public HealthSurveyResponse getHealthSurveyByUserId(Long userId) {
        HealthSurvey existingHeathSurvey = healthSurveyRepository
                .findByUserId(userId)
                .orElseThrow(() -> new HealthSurveyException(ExceptionType.HEALTH_SURVEY_NOT_FOUND));

        return mapToHealthSurveyResponse(existingHeathSurvey);
    }

    public List<String> getAllInjuriesByUserId(Long userId) {
        return healthSurveyRepository.findByUserId(userId)
                                     .map(HealthSurvey::getInjuries)
                                     .orElseGet(Collections::emptyList);
    }

    public HealthSurveyResponse updateInjuries(Long userId, List<String> newInjuries) {
        HealthSurvey healthSurvey = healthSurveyRepository.findByUserId(userId)
                                                          .orElseThrow(() -> new HealthSurveyException(
                                                                  ExceptionType.HEALTH_SURVEY_NOT_FOUND));

        healthSurvey.updateInjuries(newInjuries);
        healthSurveyRepository.save(healthSurvey);

        return mapToHealthSurveyResponse(healthSurvey);
    }


    public HealthSurveyResponse updateWeight(Long userId, Integer newWeight) {
        HealthSurvey healthSurvey = healthSurveyRepository.findByUserId(userId)
                                                          .orElseThrow(() -> new HealthSurveyException(
                                                                  ExceptionType.HEALTH_SURVEY_NOT_FOUND));

        healthSurvey.updateWeight(newWeight);
        healthSurveyRepository.save(healthSurvey);

        return mapToHealthSurveyResponse(healthSurvey);
    }

    public void deleteHealthSurveyByUserId(Long userId) {
        HealthSurvey healthSurvey = healthSurveyRepository.findByUserId(userId)
                                                          .orElseThrow(() -> new HealthSurveyException(
                                                                  ExceptionType.HEALTH_SURVEY_NOT_FOUND));
        healthSurveyRepository.delete(healthSurvey);
    }
}
