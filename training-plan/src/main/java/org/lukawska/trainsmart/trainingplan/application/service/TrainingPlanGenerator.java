package org.lukawska.trainsmart.trainingplan.application.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.lukawska.trainsmart.sharedpersistence.application.service.UserService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanRequest;
import org.lukawska.trainsmart.trainingplan.application.validation.UserExerciseValidator;
import org.lukawska.trainsmart.trainingplan.domain.entity.*;
import org.lukawska.trainsmart.trainingplan.domain.service.BlockExerciseLoadCalculator;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.IntensityLevel;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.WeekDay;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.lukawska.trainsmart.trainingplan.application.mapper.TrainingPlanMapper.mapToTrainingPlan;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class TrainingPlanGenerator {

    private final UserService userService;

    private final UserExerciseService userExerciseService;

    private final BlockExerciseLoadCalculator loadCalculator;

    private final UserExerciseValidator userExerciseValidator;

    @Transactional
    public TrainingPlan generateTrainingPlan(@NotNull Long userId, @Valid TrainingPlanRequest request) {
        User existingUser = userService.getUserById(userId);
        Map<MuscleGroup, List<UserExercise>> groups = userExerciseService.getEnabledUserExercisesByMuscleGroup(userId);
        userExerciseValidator.validateUserExercises(groups);

        TrainingPlan trainingPlan = mapToTrainingPlan(existingUser, request);

        int weekCount = request.planDuration().getWeeksCount();
        int daysPerWeek = request.preferredDays().size();
        TrainingType trainingType = trainingPlan.getTrainingType();

        log.info("Generating {} plan for {} weeks and {} days per week.", trainingType.name(), weekCount, daysPerWeek);

        Set<UserExercise> usedExercises = new HashSet<>();

        for (int week = 1; week <= weekCount; week++) {
            log.debug("Generating {} week for training plan.", week);
            TrainingWeek trainingWeek = new TrainingWeek(trainingPlan, week);

            for (int day = 1; day <= daysPerWeek; day++) {
                WeekDay scheduledDay = WeekDay.values()[day - 1];
                TrainingBlock trainingBlock =
                        generateTrainingBlock(trainingWeek, groups, usedExercises, trainingType, scheduledDay);
                trainingWeek.addTrainingBlock(trainingBlock);
            }

            trainingPlan.addTrainingWeek(trainingWeek);
        }

        return trainingPlan;
    }

    private TrainingBlock generateTrainingBlock(TrainingWeek trainingWeek, Map<MuscleGroup, List<UserExercise>> groups,
                                                Set<UserExercise> usedExercises, TrainingType trainingType,
                                                WeekDay assignedDay) {
        log.debug("Generating training block scheduled on: {} for week: {}",
                  assignedDay.name(), trainingWeek.getWeekIndex());

        TrainingBlock trainingBlock = new TrainingBlock(trainingWeek, assignedDay);
        for (MuscleGroup muscleGroup : groups.keySet()) {
            List<UserExercise> exercises = groups.get(muscleGroup);

            UserExercise pickedExercise = pickExercise(exercises, usedExercises);
            pickedExercise.recordExerciseUse();
            usedExercises.add(pickedExercise);

            BlockExercise blockExercise = generateBlockExercise(trainingBlock, pickedExercise, trainingType);
            trainingBlock.addBlockExercise(blockExercise);
        }

        return trainingBlock;
    }

    private BlockExercise generateBlockExercise(TrainingBlock trainingBlock, UserExercise userExercise,
                                                TrainingType trainingType) {
        log.debug("Generating block exercise with picked user exercise {}", userExercise.getId());

        int reps = getExerciseReps(trainingType);
        int sets = getExerciseSets(trainingType);
        IntensityLevel intensityLevel = trainingType.getDefaultIntensityLevel();

        BlockExercise blockExercise = BlockExercise.builder()
                                                   .trainingBlock(trainingBlock)
                                                   .userExercise(userExercise)
                                                   .intensity(intensityLevel)
                                                   .reps(reps)
                                                   .sets(sets)
                                                   .build();

        Double load = userExercise.isBarbellExercise() ? loadCalculator.calculateLoadPercent(blockExercise) : null;
        blockExercise.setLoadPercent(load);

        log.debug("Generated exercise block with reps: {}, sets: {}, intensity: {}, load: {}",
                  reps, sets, intensityLevel.name(), load);

        return blockExercise;
    }

    private UserExercise pickExercise(List<UserExercise> exercises, Set<UserExercise> usedExercises) {
        List<UserExercise> notUsedExercises = exercises.stream()
                                                       .filter(userExercise -> !usedExercises.contains(userExercise))
                                                       .toList();
        if (notUsedExercises.isEmpty()) {
            log.debug("All exercises performed at least once in this plan. Choosing random exercise.");
            return getRandomUserExercise(exercises);
        }

        return notUsedExercises.stream()
                               .filter(userExercise -> userExercise.getLastUsedAt() == null)
                               .findAny()
                               .map(userExercise -> {
                                   log.debug("Picked never performed exercise: {}", userExercise.getId());
                                   return userExercise;
                               })
                               .orElseGet(() -> {
                                   log.debug("All exercises used at least once. Picking exercise not used in plan");
                                   return getRandomUserExercise(notUsedExercises);
                               });
    }

    private UserExercise getRandomUserExercise(List<UserExercise> userExercises) {
        UserExercise pickedExercise = userExercises.get(ThreadLocalRandom.current().nextInt(userExercises.size()));
        log.debug("Picked exercise: {}", pickedExercise.getId());
        return pickedExercise;
    }

    private int getExerciseReps(TrainingType trainingType) {
        return ThreadLocalRandom.current().nextInt(trainingType.getMinReps(), trainingType.getMaxReps() + 1);
    }

    private int getExerciseSets(TrainingType trainingType) {
        return ThreadLocalRandom.current().nextInt(trainingType.getMinSets(), trainingType.getMaxSets() + 1);
    }
}
