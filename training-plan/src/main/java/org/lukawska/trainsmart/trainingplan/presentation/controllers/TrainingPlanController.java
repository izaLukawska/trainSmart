package org.lukawska.trainsmart.trainingplan.presentation.controllers;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.trainingplan.application.dto.response.TrainingPlanResponse;
import org.lukawska.trainsmart.trainingplan.application.service.TrainingPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/training-plans")
public class TrainingPlanController {

    private final TrainingPlanService planService;

    @GetMapping("/{planId}")
    public TrainingPlanResponse getTrainingPlanById(@PathVariable @Positive Long planId) {
        return planService.getTrainingPlanResponseByPlanId(planId);
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deleteTrainingPlanById(@PathVariable @Positive Long planId) {
        planService.deleteTrainingPlan(planId);
        return ResponseEntity.noContent().build();
    }
}
