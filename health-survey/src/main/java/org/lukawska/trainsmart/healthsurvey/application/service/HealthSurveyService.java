package org.lukawska.trainsmart.healthsurvey.application.service;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.healthsurvey.application.dto.CreateHealthSurveyRequest;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyResponse;
import org.lukawska.trainsmart.healthsurvey.application.dto.UpdateHealthSurveyRequest;
import org.lukawska.trainsmart.healthsurvey.application.exception.ExceptionType;
import org.lukawska.trainsmart.healthsurvey.application.exception.HealthSurveyException;
import org.lukawska.trainsmart.healthsurvey.domain.entites.HealthSurvey;
import org.lukawska.trainsmart.healthsurvey.domain.repositories.HealthSurveyRepository;
import org.lukawska.trainsmart.shared_persistence.application.service.UserService;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.lukawska.trainsmart.healthsurvey.application.mapper.HealthSurveyMapper.mapToEntity;
import static org.lukawska.trainsmart.healthsurvey.application.mapper.HealthSurveyMapper.mapToHealthSurveyResponse;

@Service
@RequiredArgsConstructor
public class HealthSurveyService {

    private final HealthSurveyRepository healthSurveyRepository;

    private final UserService userService;

    public HealthSurveyResponse submitHealthSurvey(CreateHealthSurveyRequest surveyRequest) {
        if (healthSurveyRepository.existsByUserId(surveyRequest.userId())) {
            throw new HealthSurveyException(ExceptionType.HEALTH_SURVEY_ALREADY_EXISTS);
        }

        User user = userService.getUserById(surveyRequest.userId());
        HealthSurvey healthSurvey = mapToEntity(surveyRequest, user);
        healthSurveyRepository.save(healthSurvey);

        return mapToHealthSurveyResponse(healthSurvey);
    }

    public HealthSurveyResponse updateHealthSurvey(UpdateHealthSurveyRequest surveyRequest) {
        HealthSurvey healthSurvey = getExistingHealthSurvey(surveyRequest.userId());

        if (surveyRequest.weight() != null) {
            healthSurvey.updateWeight(surveyRequest.weight());
        }

        if (surveyRequest.injuries() != null) {
            healthSurvey.updateInjuries(surveyRequest.injuries());
        }

        healthSurveyRepository.save(healthSurvey);

        return mapToHealthSurveyResponse(healthSurvey);
    }

    public HealthSurveyResponse getHealthSurveyByUserIdResponse(Long userId) {
        return mapToHealthSurveyResponse(getExistingHealthSurvey(userId));
    }

    public List<String> getAllInjuriesByUserId(Long userId) {
        return getExistingHealthSurvey(userId).getInjuries();
    }

    public void deleteHealthSurveyByUserId(Long userId) {
        HealthSurvey healthSurvey = getExistingHealthSurvey(userId);
        healthSurveyRepository.delete(healthSurvey);
    }

    private HealthSurvey getExistingHealthSurvey(Long userId) {
        return healthSurveyRepository
                .findByUserId(userId)
                .orElseThrow(() -> new HealthSurveyException(ExceptionType.HEALTH_SURVEY_NOT_FOUND));
    }
}
