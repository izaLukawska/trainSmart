package org.lukawska.trainsmart.trainingplan.application.service;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.lukawska.trainsmart.sharedpersistence.application.service.UserService;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanRequest;
import org.lukawska.trainsmart.trainingplan.domain.entity.*;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.IntensityLevel;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.lukawska.trainsmart.trainingplan.application.service.ExerciseLoadCalculator.calculateLoadPercent;

@Service
@RequiredArgsConstructor
public class TrainingPlanGenerator {

    private final UserService userService;

    private final UserExerciseService userExerciseService;

    @Transactional
    public TrainingPlan generateTrainingPlan(TrainingPlanRequest request) {
        Map<MuscleGroup, List<UserExercise>> groups =
                userExerciseService.getEnabledUserExercisesByMuscleGroup(request.userId());
        TrainingPlan trainingPlan = mapToTrainingPlan(request);
        TrainingType trainingType = trainingPlan.getTrainingType();
        Set<UserExercise> usedExercises = new HashSet<>();

        for (int week = 1; week <= request.duration().getWeeksCount(); week++) {
            TrainingWeek trainingWeek = new TrainingWeek(trainingPlan, week);
            for (int day = 1; day <= request.daysPerWeek(); day++) {
                TrainingBlock trainingBlock = generateTrainingBlock(trainingWeek, groups, usedExercises, trainingType);
                trainingWeek.addTrainingBlock(trainingBlock);
            }
        }

        return trainingPlan;
    }

    private TrainingBlock generateTrainingBlock(TrainingWeek trainingWeek,
                                                Map<MuscleGroup, List<UserExercise>> groups,
                                                Set<UserExercise> usedExercises,
                                                TrainingType trainingType) {
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
        int reps = getRandomReps(trainingType);
        int sets = getRandomSets(trainingType);

        IntensityLevel intensityLevel = mapToIntensityLevel(trainingType);
        Double load = userExercise.isBarbellExercise() ? calculateLoadPercent(trainingType, reps, sets) : null;

        return BlockExercise.builder()
                            .trainingBlock(trainingBlock)
                            .userExercise(userExercise)
                            .intensity(intensityLevel)
                            .reps(reps)
                            .sets(sets)
                            .calculatedWeight(load)
                            .build();
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

    private int getRandomReps(TrainingType trainingType) {
        return ThreadLocalRandom.current().nextInt(trainingType.getMinReps(), trainingType.getMaxReps() + 1);
    }

    private int getRandomSets(TrainingType trainingType) {
        return ThreadLocalRandom.current().nextInt(trainingType.getMinSets(), trainingType.getMaxSets() + 1);
    }

    private IntensityLevel mapToIntensityLevel(TrainingType trainingType) {
        return switch (trainingType) {
            case STRENGTH -> IntensityLevel.HIGH;
            case HYPERTROPHY -> IntensityLevel.MEDIUM;
            case ENDURANCE -> IntensityLevel.LIGHT;
        };
    }

    private TrainingPlan mapToTrainingPlan(TrainingPlanRequest request) {
        return new TrainingPlan(userService.getUserById(request.userId()),
                                request.trainingType(),
                                request.duration(),
                                request.daysPerWeek());
    }
}
