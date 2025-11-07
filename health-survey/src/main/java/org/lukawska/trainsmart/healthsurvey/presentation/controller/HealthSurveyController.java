package org.lukawska.trainsmart.healthsurvey.presentation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyRequest;
import org.lukawska.trainsmart.healthsurvey.application.dto.HealthSurveyResponse;
import org.lukawska.trainsmart.healthsurvey.application.dto.UpdateInjuriesRequest;
import org.lukawska.trainsmart.healthsurvey.application.dto.UpdateWeightRequest;
import org.lukawska.trainsmart.healthsurvey.application.service.HealthSurveyService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health-survey")
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
                                                                   HealthSurveyRequest surveyRequest) {
        return ResponseEntity.status(201)
                             .body(healthSurveyService.submitHealthSurvey(surveyRequest));
    }

    @PutMapping("/update-injuries/{userId}")
    public HealthSurveyResponse updateInjuries(@PathVariable @Positive Long userId,
                                               @RequestBody @Valid UpdateInjuriesRequest injuriesRequest) {
        return healthSurveyService.updateInjuries(userId, injuriesRequest.newInjuries());
    }

    @PatchMapping("/update-weight/{userId}")
    public HealthSurveyResponse updateWeight(@PathVariable Long userId,
                                             @RequestParam @Valid UpdateWeightRequest weightRequest) {
        return healthSurveyService.updateWeight(userId, weightRequest.newWeight());
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Void> deleteHealthSurvey(@PathVariable @Positive Long userId) {
        healthSurveyService.deleteHealthSurveyByUserId(userId);
        return ResponseEntity.noContent().build();
    }
}
