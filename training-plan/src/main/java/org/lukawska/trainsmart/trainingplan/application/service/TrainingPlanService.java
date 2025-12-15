package org.lukawska.trainsmart.trainingplan.application.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.sharedpersistence.application.service.UserService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanDto;
import org.lukawska.trainsmart.trainingplan.application.dto.request.PagingRequest;
import org.lukawska.trainsmart.trainingplan.application.dto.request.TrainingPlanFilterRequest;
import org.lukawska.trainsmart.trainingplan.application.dto.response.TrainingPlanResponse;
import org.lukawska.trainsmart.trainingplan.application.dto.response.TrainingPlanSummaryResponse;
import org.lukawska.trainsmart.trainingplan.application.exception.ExceptionType;
import org.lukawska.trainsmart.trainingplan.application.exception.TrainingPlanException;
import org.lukawska.trainsmart.trainingplan.application.generation.TrainingPlanGenerator;
import org.lukawska.trainsmart.trainingplan.application.preparation.dto.TrainingPlanGenerationData;
import org.lukawska.trainsmart.trainingplan.application.preparation.resolvers.TrainingPlanDataResolver;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingPlan;
import org.lukawska.trainsmart.trainingplan.domain.repositories.TrainingPlanRepository;
import org.lukawska.trainsmart.trainingplan.domain.specification.TrainingPlanSpecifications;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.Optional;

import static org.lukawska.trainsmart.trainingplan.application.mapper.training.TrainingPlanMapper.mapToTrainingPlanResponse;
import static org.lukawska.trainsmart.trainingplan.application.mapper.training.TrainingPlanMapper.mapToTrainingPlanSummary;

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
        trainingPlanRepository.save(generatedPlan);

        return mapToTrainingPlanResponse(generatedPlan);
    }

    @Transactional
    public void deleteTrainingPlanByIdAndUserId(Long planId, Long userId) {
        log.info("Deleting plan: {} for user: {}", planId, userId);
        trainingPlanRepository.deleteByIdAndUserId(planId, userId);
    }

    public Slice<TrainingPlanSummaryResponse> getAllTrainingPlansSummaryByUserId(
            Long userId, PagingRequest pagingRequest, TrainingPlanFilterRequest filterRequest) {
        log.info("Fetching all training plans for type: {} and duration: {} for user {}",
                 filterRequest.trainingType(), filterRequest.planDuration(), userId);

        Pageable pageable = PageRequest.of(pagingRequest.getPageNumber(), pagingRequest.getPageSize(),
                                           pagingRequest.getDirection(), pagingRequest.getSortBy());
        Specification<TrainingPlan> specification =
                Specification.allOf(TrainingPlanSpecifications.byUserId(userId))
                             .and(TrainingPlanSpecifications.trainingTypeEquals(filterRequest.trainingType()))
                             .and(TrainingPlanSpecifications.planDurationEquals(filterRequest.planDuration()));

        return mapToTrainingPlanSummary(trainingPlanRepository.findAll(specification, pageable));
    }

    public TrainingPlanResponse getTrainingPlanResponseByIdAndUserId(Long planId, Long userId) {
        return mapToTrainingPlanResponse(getTrainingPlanByIdAndUserId(planId, userId));
    }

    public TrainingPlan getTrainingPlanByIdAndUserId(Long planId, Long userId) {
        log.info("Fetching plan: {} for user: {}", planId, userId);
        return trainingPlanRepository.findByIdAndUserId(planId, userId).orElseThrow(
                () -> new TrainingPlanException(ExceptionType.TRAINING_PLAN_NOT_FOUND));
    }

    private Optional<Instant> getLastPlanCreationDate(Long userId) {
        return trainingPlanRepository.findMaxCreatedAtByUserId(userId);
    }
}
