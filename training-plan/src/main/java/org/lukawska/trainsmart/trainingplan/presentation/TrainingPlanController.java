package org.lukawska.trainsmart.trainingplan.presentation;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanResponse;
import org.lukawska.trainsmart.trainingplan.application.service.TrainingPlanService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/training-plans")
public class TrainingPlanController {

    private final TrainingPlanService planService;

    @GetMapping("/{planId}")
    public TrainingPlanResponse getTrainingPlanById(@PathVariable Long planId) {
        return planService.getTrainingPlanResponseByPlanId(planId);
    }
}
