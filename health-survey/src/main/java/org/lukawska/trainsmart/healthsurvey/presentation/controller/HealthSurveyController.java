package org.lukawska.trainsmart.healthsurvey.presentation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyCreateRequest;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyResponse;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyUpdateRequest;
import org.lukawska.trainsmart.healthsurvey.application.dto.WeightHistoryResponse;
import org.lukawska.trainsmart.healthsurvey.application.service.HealthSurveyService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/health-survey")
@RequiredArgsConstructor
@Validated
@Slf4j
public class HealthSurveyController {

    private final HealthSurveyService healthSurveyService;

    @GetMapping
    public HealthSurveyResponse getHealthSurvey(@PathVariable @Positive Long userId) {
        log.info("Getting health survey for user: {}", userId);
        return healthSurveyService.getHealthSurveyByUserIdResponse(userId);
    }

    @GetMapping("/injuries")
    public List<String> getAllInjuries(@PathVariable @Positive Long userId) {
        log.info("Getting injuries for user: {}", userId);
        return healthSurveyService.getAllInjuriesByUserId(userId);
    }

    @GetMapping("/weight-history")
    public List<WeightHistoryResponse> getWeightHistory(@PathVariable @Positive Long userId) {
        log.info("Getting weight history for user: {}", userId);
        return healthSurveyService.getWeightHistoryByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<HealthSurveyResponse> submitHealthSurvey(
            @PathVariable @Positive Long userId,
            @Valid @RequestBody HealthSurveyCreateRequest surveyRequest) {
        log.info("Submitting health survey for user: {}", userId);
        return ResponseEntity.status(201).body(healthSurveyService.submitHealthSurvey(userId, surveyRequest));
    }

    @PutMapping
    public ResponseEntity<HealthSurveyResponse> updateHealthSurvey(
            @PathVariable @Positive Long userId,
            @Valid @RequestBody HealthSurveyUpdateRequest surveyRequest) {
        log.info("Updating health survey for user: {}", userId);
        return ResponseEntity.ok(healthSurveyService.updateHealthSurvey(userId, surveyRequest));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteHealthSurvey(@PathVariable @Positive Long userId) {
        log.info("Deleting health survey for user: {}", userId);
        healthSurveyService.deleteHealthSurveyByUserId(userId);
        return ResponseEntity.noContent().build();
    }
}
