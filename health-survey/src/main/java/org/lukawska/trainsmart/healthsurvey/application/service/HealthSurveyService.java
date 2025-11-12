package org.lukawska.trainsmart.healthsurvey.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        log.info("Saved health survey: {}", healthSurvey.getId());

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
        log.info("Updated health survey for ID: {}", healthSurvey.getId());

        return mapToHealthSurveyResponse(healthSurvey);
    }

    @Transactional(readOnly = true)
    public HealthSurveyResponse getHealthSurveyByUserIdResponse(Long userId) {
        HealthSurvey foundHealthSurvey = getExistingHealthSurvey(userId);
        log.info("Found health survey: {}", foundHealthSurvey.getId());

        return mapToHealthSurveyResponse(foundHealthSurvey);
    }

    @Transactional(readOnly = true)
    public List<String> getAllInjuriesByUserId(Long userId) {
        List<String> injuries = healthSurveyRepository.findAllInjuriesByUserId(userId).orElseThrow(
                () -> new HealthSurveyException(ExceptionType.HEALTH_SURVEY_NOT_FOUND));

        log.info("Found {} injuries.", injuries.size());
        return injuries;
    }

    @Transactional
    public void deleteHealthSurveyByUserId(Long userId) {
        if (!healthSurveyRepository.existsByUserId(userId)) {
            throw new HealthSurveyException(ExceptionType.HEALTH_SURVEY_NOT_FOUND);
        }

        healthSurveyRepository.deleteByUserId(userId);
        log.info("Deleted health survey for user: {}", userId);
    }

    @Transactional(readOnly = true)
    public List<WeightHistoryResponse> getWeightHistoryByUserId(Long userId) {
        Long healthSurveyId = getExistingHealthSurvey(userId).getId();
        log.info("Fetching weight history for health survey: {}", healthSurveyId);

        List<WeightHistoryResponse> weightHistory = healthSurveyRepository.findRevisions(healthSurveyId)
                                                                          .stream()
                                                                          .map(rev -> new WeightHistoryResponse(
                                                                                  rev.getEntity().getWeight(),
                                                                                  rev.getRevisionInstant()
                                                                                     .orElse(Instant.EPOCH)))
                                                                          .toList();
        log.debug("Found {} weight update records.", weightHistory.size());

        return weightHistory;
    }

    private HealthSurvey getExistingHealthSurvey(Long userId) {
        HealthSurvey foundHealthSurvey = healthSurveyRepository.findByUserId(userId).orElseThrow(
                () -> new HealthSurveyException(ExceptionType.HEALTH_SURVEY_NOT_FOUND));
        log.info("Found health survey: {}.", foundHealthSurvey.getId());

        return foundHealthSurvey;
    }
}
