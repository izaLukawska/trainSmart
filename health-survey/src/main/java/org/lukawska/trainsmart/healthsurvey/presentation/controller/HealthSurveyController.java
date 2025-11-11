package org.lukawska.trainsmart.healthsurvey.presentation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyCreateRequest;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyResponse;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyUpdateRequest;
import org.lukawska.trainsmart.healthsurvey.application.service.HealthSurveyService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/health-survey")
@RequiredArgsConstructor
@Validated
public class HealthSurveyController {

    private final HealthSurveyService healthSurveyService;

    @GetMapping
    public HealthSurveyResponse getHealthSurveyByUserId(@PathVariable @Positive Long userId) {
        return healthSurveyService.getHealthSurveyByUserIdResponse(userId);
    }

    @GetMapping("/injuries")
    public List<String> getAllInjuriesByUserId(@PathVariable @Positive Long userId) {
        return healthSurveyService.getAllInjuriesByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<HealthSurveyResponse> submitHealthSurvey(
            @PathVariable @Positive Long userId,
            @Valid @RequestBody HealthSurveyCreateRequest surveyRequest) {
        return ResponseEntity.status(201).body(healthSurveyService.submitHealthSurvey(userId, surveyRequest));
    }

    @PutMapping
    public ResponseEntity<HealthSurveyResponse> updateHealthSurvey(
            @PathVariable @Positive Long userId,
            @Valid @RequestBody HealthSurveyUpdateRequest surveyRequest) {
        return ResponseEntity.ok(healthSurveyService.updateHealthSurvey(userId, surveyRequest));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteHealthSurvey(@PathVariable @Positive Long userId) {
        healthSurveyService.deleteHealthSurveyByUserId(userId);
        return ResponseEntity.noContent().build();
    }
}
