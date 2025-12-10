package org.lukawska.trainsmart.trainingplan.application.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.sharedpersistence.application.service.UserService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanRequest;
import org.lukawska.trainsmart.trainingplan.application.generation.TrainingPlanGenerator;
import org.lukawska.trainsmart.trainingplan.application.preparation.dto.TrainingPlanGenerationData;
import org.lukawska.trainsmart.trainingplan.application.preparation.resolvers.TrainingPlanDataResolver;
import org.lukawska.trainsmart.trainingplan.domain.entity.TrainingPlan;
import org.lukawska.trainsmart.trainingplan.domain.repository.TrainingPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class TrainingPlanService {

    private final TrainingPlanRepository trainingPlanRepository;

    private final UserService userService;

    private final TrainingPlanGenerator trainingPlanGenerator;

    private final TrainingPlanDataResolver trainingPlanDataResolver;

    @Transactional
    public TrainingPlan createTrainingPlanForUser(@NotNull Long userId, @Valid TrainingPlanRequest request) {
        User existingUser = userService.getUserById(userId);
        TrainingPlanGenerationData generationData = trainingPlanDataResolver.getResolvedData(
                existingUser, request, getLastPlanCreationDate(userId));

        TrainingPlan generatedPlan = trainingPlanGenerator.generateTrainingPlan(generationData);
        trainingPlanRepository.save(generatedPlan);

        return generatedPlan;
    }

    public Optional<Instant> getLastPlanCreationDate(Long userId) {
        return trainingPlanRepository.findTopCreatedAtByUserIdOrderByCreatedAtDesc(userId);
    }
}
