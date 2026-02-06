package org.lukawska.trainsmart.healthsurvey.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.healthsurvey.api.HealthSurveyApi;
import org.lukawska.trainsmart.healthsurvey.application.service.HealthSurveyService;
import org.lukawska.trainsmart.healthsurvey.model.HealthSurveyCreateRequest;
import org.lukawska.trainsmart.healthsurvey.model.HealthSurveyResponse;
import org.lukawska.trainsmart.healthsurvey.model.HealthSurveyUpdateRequest;
import org.lukawska.trainsmart.healthsurvey.model.WeightHistoryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users/{userId}/health-survey")
@RequiredArgsConstructor
@Validated
@Slf4j
public class HealthSurveyController implements HealthSurveyApi {

    private final HealthSurveyService healthSurveyService;

    @Override
    public ResponseEntity<Void> deleteHealthSurvey(Long userId) {
        log.info("Received delete health survey request for user: {}", userId);
        healthSurveyService.deleteHealthSurveyByUserId(userId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Set<String>> getAllInjuries(Long userId) {
        log.info("Received get all injuries request for user {}", userId);
        return ResponseEntity.ok(healthSurveyService.getAllInjuriesByUserId(userId));
    }

    @Override
    public ResponseEntity<HealthSurveyResponse> getHealthSurvey(Long userId) {
        log.info("Received get health survey request for user {},", userId);
        return ResponseEntity.ok(healthSurveyService.getHealthSurveyByUserIdResponse(userId));
    }

    @Override
    public ResponseEntity<List<WeightHistoryResponse>> getWeightHistory(Long userId) {
        log.info("Received get weight history request for user: {}", userId);
        return ResponseEntity.ok(healthSurveyService.getWeightHistoryByUserId(userId));
    }

    @PreAuthorize("hasRole('USER')")
    @Override
    public ResponseEntity<HealthSurveyResponse> submitHealthSurvey(Long userId, HealthSurveyCreateRequest request) {
        log.info("Received submit health survey request for user: {}", userId);
        return ResponseEntity.status(201).body(healthSurveyService.submitHealthSurvey(userId, request));
    }

    @PreAuthorize("hasRole('USER')")
    @Override
    public ResponseEntity<HealthSurveyResponse> updateHealthSurvey(Long userId, HealthSurveyUpdateRequest request) {
        log.info("Received update health survey request for user: {}", userId);
        return ResponseEntity.ok(healthSurveyService.updateHealthSurvey(userId, request));
    }
}
