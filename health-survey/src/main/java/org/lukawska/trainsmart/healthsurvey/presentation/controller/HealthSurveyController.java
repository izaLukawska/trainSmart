package org.lukawska.trainsmart.healthsurvey.presentation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.healthsurvey.application.dto.CreateHealthSurveyRequest;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyResponse;
import org.lukawska.trainsmart.healthsurvey.application.dto.UpdateHealthSurveyRequest;
import org.lukawska.trainsmart.healthsurvey.application.service.HealthSurveyService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/health-survey")
@RequiredArgsConstructor
@Validated
public class HealthSurveyController {

    private final HealthSurveyService healthSurveyService;

    @GetMapping("/{userId}")
    public HealthSurveyResponse getHealthSurveyByUserId(@PathVariable @Positive Long userId) {
        return healthSurveyService.getHealthSurveyByUserIdResponse(userId);
    }

    @GetMapping("/{userId}")
    public List<String> getAllInjuriesByUserId(@PathVariable @Positive Long userId) {
        return healthSurveyService.getAllInjuriesByUserId(userId);
    }

    @PostMapping("/submit")
    public ResponseEntity<HealthSurveyResponse> submitHealthSurvey(@Valid
                                                                   @RequestBody
                                                                   CreateHealthSurveyRequest surveyRequest) {
        return ResponseEntity.status(201).body(healthSurveyService.submitHealthSurvey(surveyRequest));
    }

    @PutMapping("/update")
    public ResponseEntity<HealthSurveyResponse> updateHealthSurvey(@Valid
                                                                   @RequestBody
                                                                   UpdateHealthSurveyRequest surveyRequest) {
        return ResponseEntity.ok(healthSurveyService.updateHealthSurvey(surveyRequest));
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Void> deleteHealthSurvey(@PathVariable @Positive Long userId) {
        healthSurveyService.deleteHealthSurveyByUserId(userId);
        return ResponseEntity.noContent().build();
    }
}
