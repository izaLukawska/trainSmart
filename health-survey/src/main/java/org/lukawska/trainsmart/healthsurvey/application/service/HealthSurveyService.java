package org.lukawska.trainsmart.healthsurvey.application.service;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyCreateRequest;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyResponse;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyUpdateRequest;
import org.lukawska.trainsmart.healthsurvey.application.dto.WeightHistoryResponse;
import org.lukawska.trainsmart.healthsurvey.application.exception.ExceptionType;
import org.lukawska.trainsmart.healthsurvey.application.exception.HealthSurveyException;
import org.lukawska.trainsmart.healthsurvey.domain.entites.HealthSurvey;
import org.lukawska.trainsmart.healthsurvey.domain.repositories.HealthSurveyRepository;
import org.lukawska.trainsmart.shared_persistence.application.service.UserService;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static org.lukawska.trainsmart.healthsurvey.application.mapper.HealthSurveyMapper.mapToEntity;
import static org.lukawska.trainsmart.healthsurvey.application.mapper.HealthSurveyMapper.mapToHealthSurveyResponse;

@Service
@RequiredArgsConstructor
public class HealthSurveyService {

    private final HealthSurveyRepository healthSurveyRepository;

    private final UserService userService;

    @Transactional
    public HealthSurveyResponse submitHealthSurvey(Long userId, HealthSurveyCreateRequest surveyRequest) {
        if (healthSurveyRepository.existsByUserId(userId)) {
            throw new HealthSurveyException(ExceptionType.HEALTH_SURVEY_ALREADY_EXISTS);
        }

        User user = userService.getUserById(userId);
        HealthSurvey healthSurvey = mapToEntity(surveyRequest, user);
        healthSurveyRepository.save(healthSurvey);

        return mapToHealthSurveyResponse(healthSurvey);
    }

    @Transactional
    public HealthSurveyResponse updateHealthSurvey(Long userId, HealthSurveyUpdateRequest surveyRequest) {
        HealthSurvey healthSurvey = getExistingHealthSurvey(userId);

        if (surveyRequest.weight() != null) {
            healthSurvey.updateWeight(surveyRequest.weight());
        }

        if (surveyRequest.injuries() != null) {
            healthSurvey.updateInjuries(surveyRequest.injuries());
        }

        healthSurveyRepository.save(healthSurvey);

        return mapToHealthSurveyResponse(healthSurvey);
    }

    @Transactional(readOnly = true)
    public HealthSurveyResponse getHealthSurveyByUserIdResponse(Long userId) {
        return mapToHealthSurveyResponse(getExistingHealthSurvey(userId));
    }

    @Transactional(readOnly = true)
    public List<String> getAllInjuriesByUserId(Long userId) {
        return healthSurveyRepository.findAllInjuriesByUserId(userId)
                                     .orElseThrow(() -> new HealthSurveyException(
                                             ExceptionType.HEALTH_SURVEY_NOT_FOUND));
    }

    @Transactional
    public void deleteHealthSurveyByUserId(Long userId) {
        if (!healthSurveyRepository.existsByUserId(userId)) {
            throw new HealthSurveyException(ExceptionType.HEALTH_SURVEY_NOT_FOUND);
        }

        healthSurveyRepository.deleteByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<WeightHistoryResponse> getWeightHistoryByUserId(Long userId) {
        Long healthSurveyId = healthSurveyRepository.findIdByUserId(userId)
                                                    .orElseThrow(() -> new HealthSurveyException(
                                                            ExceptionType.HEALTH_SURVEY_NOT_FOUND));

        return healthSurveyRepository.findRevisions(healthSurveyId)
                                     .stream()
                                     .map(rev -> new WeightHistoryResponse(
                                             rev.getEntity().getWeight(),
                                             rev.getRevisionInstant().orElse(Instant.EPOCH)))
                                     .toList();
    }

    private HealthSurvey getExistingHealthSurvey(Long userId) {
        return healthSurveyRepository.findByUserId(userId)
                                     .orElseThrow(() -> new HealthSurveyException(
                                             ExceptionType.HEALTH_SURVEY_NOT_FOUND));
    }
}
