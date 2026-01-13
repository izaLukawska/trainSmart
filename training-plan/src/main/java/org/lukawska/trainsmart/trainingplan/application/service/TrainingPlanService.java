package org.lukawska.trainsmart.trainingplan.application.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.sharedpersistence.application.service.UserService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanDto;
import org.lukawska.trainsmart.trainingplan.application.dto.request.TrainingPlanFilterRequest;
import org.lukawska.trainsmart.trainingplan.application.dto.response.TrainingPlanResponse;
import org.lukawska.trainsmart.trainingplan.application.dto.response.TrainingPlanSummaryResponse;
import org.lukawska.trainsmart.trainingplan.application.exception.ExceptionType;
import org.lukawska.trainsmart.trainingplan.application.exception.TrainingPlanException;
import org.lukawska.trainsmart.trainingplan.application.generation.TrainingPlanGenerator;
import org.lukawska.trainsmart.trainingplan.application.preparation.dto.TrainingPlanGenerationData;
import org.lukawska.trainsmart.trainingplan.application.preparation.resolvers.TrainingPlanDataResolver;
import org.lukawska.trainsmart.trainingplan.application.specification.TrainingPlanSpecificationBuilder;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingPlan;
import org.lukawska.trainsmart.trainingplan.domain.repositories.TrainingPlanRepository;
import org.lukawska.trainsmart.trainingplan.model.PagingRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.Optional;

import static org.lukawska.trainsmart.trainingplan.application.mapper.PageableMapper.mapToPageable;
import static org.lukawska.trainsmart.trainingplan.application.mapper.TrainingPlanMapper.mapToTrainingPlanResponse;
import static org.lukawska.trainsmart.trainingplan.application.mapper.TrainingPlanMapper.mapToTrainingPlanSummarySlice;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class TrainingPlanService {

    private final TrainingPlanRepository trainingPlanRepository;

    private final UserService userService;

    private final TrainingPlanGenerator trainingPlanGenerator;

    private final TrainingPlanDataResolver trainingPlanDataResolver;

    @Transactional
    public TrainingPlanResponse createTrainingPlan(@NotNull Long userId, @Valid TrainingPlanDto request) {
        log.info("Creating plan for user: {}", userId);
        User existingUser = userService.getUserById(userId);
        TrainingPlanGenerationData generationData = trainingPlanDataResolver.getResolvedData(
                existingUser, request, getLastPlanCreationDate(userId));
        TrainingPlan generatedPlan = trainingPlanGenerator.generateTrainingPlan(generationData);

        try {
            trainingPlanRepository.save(generatedPlan);
            log.info("Successfully generated plan with ID {}", generatedPlan.getId());
            return mapToTrainingPlanResponse(generatedPlan);

        } catch (DataIntegrityViolationException e) {
            throw new TrainingPlanException(ExceptionType.INVALID_TRAINING_PLAN_DATA);
        }
    }

    @Transactional
    public void deleteTrainingPlanByIdAndUserId(Long planId, Long userId) {
        trainingPlanRepository.deleteByIdAndUserId(planId, userId);
        log.info("Deleted plan: {} for user: {}", planId, userId);
    }

    public Slice<TrainingPlanSummaryResponse> getAllTrainingPlansSummaryByUserId(
            Long userId, PagingRequest pagingRequest, TrainingPlanFilterRequest filterRequest) {
        Pageable pageable = mapToPageable(pagingRequest);
        Specification<TrainingPlan> specification = TrainingPlanSpecificationBuilder.build(userId, filterRequest);
        Slice<TrainingPlan> foundTrainingPlans = trainingPlanRepository.findAll(specification, pageable);

        log.info("Found {} training plans", foundTrainingPlans.getSize());
        return mapToTrainingPlanSummarySlice(foundTrainingPlans);
    }

    public TrainingPlanResponse getTrainingPlanResponseByIdAndUserId(Long planId, Long userId) {
        return mapToTrainingPlanResponse(getTrainingPlanByIdAndUserId(planId, userId));
    }

    public TrainingPlan getTrainingPlanByIdAndUserId(Long planId, Long userId) {
        TrainingPlan trainingPlan = trainingPlanRepository.findByIdAndUserId(planId, userId).orElseThrow(
                () -> new TrainingPlanException(ExceptionType.TRAINING_PLAN_NOT_FOUND));
        log.info("Found training plan {}", planId);

        return trainingPlan;
    }

    private Optional<Instant> getLastPlanCreationDate(Long userId) {
        return trainingPlanRepository.findMaxCreatedAtByUserId(userId);
    }
}
