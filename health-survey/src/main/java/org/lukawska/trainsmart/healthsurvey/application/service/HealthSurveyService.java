package org.lukawska.trainsmart.healthsurvey.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyCreateRequest;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyResponse;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyUpdateRequest;
import org.lukawska.trainsmart.healthsurvey.application.dto.WeightHistoryResponse;
import org.lukawska.trainsmart.healthsurvey.application.exception.ExceptionType;
import org.lukawska.trainsmart.healthsurvey.application.exception.HealthSurveyException;
import org.lukawska.trainsmart.healthsurvey.application.mapper.HealthSurveyMapper;
import org.lukawska.trainsmart.healthsurvey.domain.entites.HealthSurvey;
import org.lukawska.trainsmart.healthsurvey.domain.repositories.HealthSurveyRepository;
import org.lukawska.trainsmart.sharedpersistence.application.service.UserService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        User user = userService.getUserById(userId);
        log.info("Creating health survey for user: {}", user.getId());
        HealthSurvey healthSurvey = mapToEntity(surveyRequest, user);

        try {
            healthSurveyRepository.save(healthSurvey);
            log.info("Saved health survey: {}", healthSurvey.getId());
            return mapToHealthSurveyResponse(healthSurvey);
        } catch (DataIntegrityViolationException e) {
            log.warn("Failed to create health survey for user {}: {}", userId, e.getMessage());
            throw new HealthSurveyException(ExceptionType.HEALTH_SURVEY_ALREADY_EXISTS);
        }
    }

    @Transactional
    public HealthSurveyResponse updateHealthSurvey(Long userId, HealthSurveyUpdateRequest healthSurveyUpdateRequest) {
        HealthSurvey healthSurvey = getExistingHealthSurvey(userId);

        log.info("Updating health survey for user: {}", userId);
        updateWeight(healthSurveyUpdateRequest, healthSurvey);
        updateInjuries(healthSurveyUpdateRequest, healthSurvey);
        healthSurveyRepository.save(healthSurvey);

        return mapToHealthSurveyResponse(healthSurvey);
    }

    public HealthSurveyResponse getHealthSurveyByUserIdResponse(Long userId) {
        HealthSurvey healthSurvey = getExistingHealthSurvey(userId);
        log.info("Found health survey: {}", healthSurvey.getId());

        return mapToHealthSurveyResponse(healthSurvey);
    }

    public HealthSurvey getExistingHealthSurvey(Long userId) {
        HealthSurvey healthSurvey = healthSurveyRepository.findByUserId(userId).orElseThrow(
                () -> new HealthSurveyException(ExceptionType.HEALTH_SURVEY_NOT_FOUND));
        log.info("Found health survey: {}.", healthSurvey.getId());

        return healthSurvey;
    }

    public Set<String> getAllInjuriesByUserId(Long userId) {
        Set<String> injuries = healthSurveyRepository.findAllInjuriesByUserId(userId).orElseThrow(
                () -> new HealthSurveyException(ExceptionType.HEALTH_SURVEY_NOT_FOUND));
        log.info("Found {} injuries.", injuries.size());

        return injuries;
    }

    @Transactional
    public void deleteHealthSurveyByUserId(Long userId) {
        HealthSurvey healthSurvey = healthSurveyRepository.findByUserId(userId).orElseThrow(
                () -> new HealthSurveyException(ExceptionType.HEALTH_SURVEY_NOT_FOUND));
        log.info("Deleting health survey {}", healthSurvey.getId());

        healthSurveyRepository.delete(healthSurvey);
    }

    public List<WeightHistoryResponse> getWeightHistoryByUserId(Long userId) {
        Long healthSurveyId = getExistingHealthSurvey(userId).getId();
        log.info("Fetching weight history for health survey: {}", healthSurveyId);

        List<WeightHistoryResponse> weightHistory =
                healthSurveyRepository.findRevisions(healthSurveyId)
                                      .stream()
                                      .map(HealthSurveyMapper::mapToWeightHistoryResponse)
                                      .toList();
        log.debug("Found {} weight update records.", weightHistory.size());

        return weightHistory;
    }

    private void updateWeight(HealthSurveyUpdateRequest updateRequest, HealthSurvey healthSurvey) {
        Optional.ofNullable(updateRequest.getWeight())
                .ifPresentOrElse(newWeight -> {
                    healthSurvey.updateWeight(newWeight);
                    log.info("Updated weight: {} for health survey: {}", newWeight, healthSurvey.getId());
                }, () -> log.debug("No weight provided for health survey: {}", healthSurvey.getId()));
    }

    private void updateInjuries(HealthSurveyUpdateRequest updateRequest, HealthSurvey healthSurvey) {
        Optional.ofNullable(updateRequest.getInjuries())
                .ifPresentOrElse(newInjuries -> {
                    healthSurvey.updateInjuries(newInjuries);
                    log.info("Updated {} injuries for health survey {}", newInjuries.size(), healthSurvey.getId());
                }, () -> log.debug("No injuries provided for health survey {}", healthSurvey.getId()));
    }
}
