package org.lukawska.trainsmart.trainingplan.presentation.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.trainingplan.api.TrainingPlanApi;
import org.lukawska.trainsmart.trainingplan.application.service.TrainingPlanService;
import org.lukawska.trainsmart.trainingplan.model.PagingRequest;
import org.lukawska.trainsmart.trainingplan.model.SliceTrainingPlanSummaryResponse;
import org.lukawska.trainsmart.trainingplan.model.TrainingPlanFilterRequest;
import org.lukawska.trainsmart.trainingplan.model.TrainingPlanResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TrainingPlanController implements TrainingPlanApi {

    private final TrainingPlanService trainingPlanService;

    @Override
    public ResponseEntity<TrainingPlanResponse> getTrainingPlanByIdAndUserId(Long userId, Long planId) {
        log.info("Received request for get training plan for user {} and plan {}", userId, planId);
        return ResponseEntity.ok().body(trainingPlanService.getTrainingPlanResponseByIdAndUserId(planId, userId));
    }

    @Override
    public ResponseEntity<Void> deleteTrainingPlanByIdAndUserId(Long userId, Long planId) {
        log.info("Received delete request for training plan {} for user {}", planId, userId);
        trainingPlanService.deleteTrainingPlanByIdAndUserId(planId, userId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<SliceTrainingPlanSummaryResponse> getAllTrainingPlansByUserId(
            Long userId, PagingRequest pagingRequest, TrainingPlanFilterRequest trainingPlanFilterRequest) {
        log.info("Received request to get all training plan for user {}", userId);
        return ResponseEntity.ok().body(trainingPlanService.getAllTrainingPlansSummaryByUserId(
                userId, pagingRequest, trainingPlanFilterRequest));
    }
}
