package org.lukawska.trainsmart.trainingplan.application.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.sharedpersistence.application.service.UserService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanDto;
import org.lukawska.trainsmart.trainingplan.application.dto.response.TrainingPlanResponse;
import org.lukawska.trainsmart.trainingplan.application.exception.ExceptionType;
import org.lukawska.trainsmart.trainingplan.application.exception.TrainingPlanException;
import org.lukawska.trainsmart.trainingplan.application.generation.TrainingPlanGenerator;
import org.lukawska.trainsmart.trainingplan.application.mapper.TrainingPlanMapper;
import org.lukawska.trainsmart.trainingplan.application.preparation.dto.TrainingPlanGenerationData;
import org.lukawska.trainsmart.trainingplan.application.preparation.resolvers.TrainingPlanDataResolver;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingPlan;
import org.lukawska.trainsmart.trainingplan.domain.repositories.TrainingPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class TrainingPlanService {

    private final TrainingPlanRepository trainingPlanRepository;

    private final UserService userService;

    private final TrainingPlanGenerator trainingPlanGenerator;

    private final TrainingPlanDataResolver trainingPlanDataResolver;

    private final TrainingPlanMapper trainingPlanMapper;

    @Transactional
    public TrainingPlanResponse createTrainingPlan(@NotNull Long userId, @Valid TrainingPlanDto request) {
        User existingUser = userService.getUserById(userId);
        TrainingPlanGenerationData generationData = trainingPlanDataResolver.getResolvedData(
                existingUser, request, getLastPlanCreationDate(userId));

        TrainingPlan generatedPlan = trainingPlanGenerator.generateTrainingPlan(generationData);
        trainingPlanRepository.save(generatedPlan);

        return trainingPlanMapper.toResponse(generatedPlan);
    }

    @Transactional
    public void deleteTrainingPlan(Long planId) {
        log.info("Deleting plan: {}", planId);
        trainingPlanRepository.deleteById(planId);
    }

    public TrainingPlanResponse getTrainingPlanResponseByPlanId(Long planId) {
        return trainingPlanMapper.toResponse(getTrainingPlanPlanId(planId));
    }

    public TrainingPlan getTrainingPlanPlanId(Long planId) {
        log.info("Fetching plan: {}", planId);
        return trainingPlanRepository.findById(planId).orElseThrow(
                () -> new TrainingPlanException(ExceptionType.TRAINING_PLAN_NOT_FOUND));
    }

    private Optional<Instant> getLastPlanCreationDate(Long userId) {
        return trainingPlanRepository.findMaxCreatedAtByUserId(userId);
    }
}
