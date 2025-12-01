package org.lukawska.trainsmart.trainingplan.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.lukawska.trainsmart.sharedpersistence.application.service.UserService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanRequest;
import org.lukawska.trainsmart.trainingplan.domain.entity.*;
import org.lukawska.trainsmart.trainingplan.domain.service.BlockExerciseLoadCalculator;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.IntensityLevel;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.lukawska.trainsmart.trainingplan.application.mapper.TrainingPlanMapper.mapToTrainingPlan;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingPlanGenerator {

    private final UserService userService;

    private final UserExerciseService userExerciseService;

    private final BlockExerciseLoadCalculator loadCalculator;

    @Transactional
    public TrainingPlan generateTrainingPlan(Long userId, TrainingPlanRequest request) {
        User existingUser = userService.getUserById(userId);
        TrainingPlan trainingPlan = mapToTrainingPlan(existingUser, request);

        int weekCount = request.planDuration().getWeeksCount();
        int daysPerkWeek = request.daysPerWeek();
        TrainingType trainingType = trainingPlan.getTrainingType();

        log.info("Generating {} plan for {} weeks and {} days per week.", trainingType.name(), weekCount, daysPerkWeek);

        Set<UserExercise> usedExercises = new HashSet<>();

        for (int week = 1; week <= weekCount; week++) {
            TrainingWeek trainingWeek = new TrainingWeek(trainingPlan, week);

            for (int day = 1; day <= daysPerkWeek; day++) {
                TrainingBlock trainingBlock = generateTrainingBlock(trainingWeek, userId, usedExercises, trainingType);
                trainingWeek.addTrainingBlock(trainingBlock);
            }

            trainingPlan.addTrainingWeek(trainingWeek);
        }

        return trainingPlan;
    }

    private TrainingBlock generateTrainingBlock(TrainingWeek trainingWeek,
                                                Long userId,
                                                Set<UserExercise> usedExercises,
                                                TrainingType trainingType) {

        Map<MuscleGroup, List<UserExercise>> groups = userExerciseService.getEnabledUserExercisesByMuscleGroup(userId);

        TrainingBlock trainingBlock = new TrainingBlock(trainingWeek);
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

    private BlockExercise generateBlockExercise(TrainingBlock trainingBlock,
                                                UserExercise userExercise,
                                                TrainingType trainingType) {
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

        return blockExercise;
    }

    private UserExercise pickExercise(List<UserExercise> exercises, Set<UserExercise> usedExercises) {
        List<UserExercise> notUsedExercises = exercises.stream()
                                                       .filter(userExercise -> !usedExercises.contains(userExercise))
                                                       .toList();
        if (notUsedExercises.isEmpty()) {
            return getRandomUserExercise(exercises);
        }

        return notUsedExercises.stream()
                               .filter(userExercise -> userExercise.getLastUsedAt() == null)
                               .findAny()
                               .orElseGet(() -> getRandomUserExercise(notUsedExercises));
    }

    private UserExercise getRandomUserExercise(List<UserExercise> userExercises) {
        return userExercises.get(ThreadLocalRandom.current().nextInt(userExercises.size()));
    }

    private int getExerciseReps(TrainingType trainingType) {
        return ThreadLocalRandom.current().nextInt(trainingType.getMinReps(), trainingType.getMaxReps() + 1);
    }

    private int getExerciseSets(TrainingType trainingType) {
        return ThreadLocalRandom.current().nextInt(trainingType.getMinSets(), trainingType.getMaxSets() + 1);
    }
}
