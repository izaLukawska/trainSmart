package org.lukawska.trainsmart.trainingplan.presentation.controllers;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.trainingplan.application.dto.request.PagingRequest;
import org.lukawska.trainsmart.trainingplan.application.dto.request.TrainingPlanFilterRequest;
import org.lukawska.trainsmart.trainingplan.application.dto.response.TrainingPlanResponse;
import org.lukawska.trainsmart.trainingplan.application.dto.response.TrainingPlanSummaryResponse;
import org.lukawska.trainsmart.trainingplan.application.service.TrainingPlanService;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/user/{userId}/training-plans")
public class TrainingPlanController {

    private final TrainingPlanService trainingPlanService;

    @GetMapping("/{planId}")
    public TrainingPlanResponse getTrainingPlanByIdAndUserId(@PathVariable @Positive Long planId,
                                                             @PathVariable @Positive Long userId) {
        return trainingPlanService.getTrainingPlanResponseByIdAndUserId(planId, userId);
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deleteTrainingPlanByIdAndUserId(@PathVariable @Positive Long planId,
                                                                @PathVariable @Positive Long userId) {
        trainingPlanService.deleteTrainingPlanByIdAndUserId(planId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public Slice<TrainingPlanSummaryResponse> getAllTrainingPlansByUserId(
            @PathVariable @Positive Long userId,
            @ModelAttribute PagingRequest pagingRequest,
            @ModelAttribute TrainingPlanFilterRequest filterRequest) {
        return trainingPlanService.getAllTrainingPlansSummaryByUserId(userId, pagingRequest, filterRequest);
    }
}
