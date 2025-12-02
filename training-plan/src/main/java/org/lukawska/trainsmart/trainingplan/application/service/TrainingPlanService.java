package org.lukawska.trainsmart.trainingplan.application.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.lukawska.trainsmart.sharedpersistence.application.service.UserService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanRequest;
import org.lukawska.trainsmart.trainingplan.application.validation.UserExerciseValidator;
import org.lukawska.trainsmart.trainingplan.domain.entity.TrainingPlan;
import org.lukawska.trainsmart.trainingplan.domain.entity.UserExercise;
import org.lukawska.trainsmart.trainingplan.domain.repository.TrainingPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class TrainingPlanService {

    private final TrainingPlanRepository trainingPlanRepository;

    private final UserService userService;

    private final TrainingPlanGenerator trainingPlanGenerator;

    private final TrainingPlanExerciseSelector exerciseSelector;

    private final UserExerciseValidator userExerciseValidator;

    @Transactional
    public TrainingPlan createTrainingPlanForUser(@NotNull Long userId, @Valid TrainingPlanRequest request) {
        User existingUser = userService.getUserById(userId);

        Map<MuscleGroup, List<UserExercise>> muscleGroups = exerciseSelector.selectExercises(userId);
        userExerciseValidator.validateUserExercises(muscleGroups);

        TrainingPlan generatedPlan = trainingPlanGenerator.generateTrainingPlan(existingUser, muscleGroups, request);
        trainingPlanRepository.save(generatedPlan);

        return generatedPlan;
    }

    public Optional<Instant> getLastPlanCreationDate(Long userId) {
        return trainingPlanRepository.findTopCreatedAtByUserIdOrderByCreatedAtDesc(userId);
    }
}
