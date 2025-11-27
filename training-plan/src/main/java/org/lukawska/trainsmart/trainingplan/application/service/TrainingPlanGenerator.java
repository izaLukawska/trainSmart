package org.lukawska.trainsmart.trainingplan.application.service;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.lukawska.trainsmart.sharedpersistence.application.service.UserService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.application.exception.ExceptionType;
import org.lukawska.trainsmart.trainingplan.application.exception.TrainingPlanException;
import org.lukawska.trainsmart.trainingplan.domain.entity.*;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.Duration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.Intensity;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class TrainingPlanGenerator {

    private final UserService userService;

    private final UserExerciseService userExerciseService;

    public TrainingPlan generatePlan(Long userId, Duration duration, int daysPerWeek, TrainingType trainingType) {
        int weekCount = duration.getWeeksCount();
        User user = userService.getUserById(userId);
        TrainingPlan trainingPlan = new TrainingPlan(user, trainingType, duration, daysPerWeek);

        Map<MuscleGroup, List<UserExercise>> exercises = userExerciseService.getEnabledUserExercisesByMuscleGroup(
                userId);

        if (exercises.size() < 3) {
            throw new TrainingPlanException(ExceptionType.NOT_ENOUGH_EXERCISES);
        }

        Map<MuscleGroup, Set<Long>> usedIdsPerGroup = new HashMap<>();

        for (int week = 1; week <= weekCount; week++) {
            TrainingWeek trainingWeek = new TrainingWeek(trainingPlan, week);
            for (int day = 1; day <= daysPerWeek; day++) {
                TrainingBlock trainingBlock = generateTrainingBlock(trainingWeek, exercises, usedIdsPerGroup);
                trainingWeek.addTrainingBlock(trainingBlock);
            }

            trainingPlan.addTrainingWeek(trainingWeek);
        }

        return trainingPlan;
    }

    public TrainingBlock generateTrainingBlock(TrainingWeek trainingWeek,
                                               Map<MuscleGroup, List<UserExercise>> groupedExercises,
                                               Map<MuscleGroup, Set<Long>> usedIdsPerGroup) {
        TrainingBlock trainingBlock = new TrainingBlock(trainingWeek);
        for (MuscleGroup muscleGroup : groupedExercises.keySet()) {
            List<UserExercise> exercises = groupedExercises.get(muscleGroup);

            Set<Long> usedIds = usedIdsPerGroup.computeIfAbsent(muscleGroup, k -> new HashSet<>());
            List<UserExercise> candidates = getCandidates(exercises, usedIds);

            UserExercise chosen = candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
            chosen.recordExerciseUse();
            usedIds.add(chosen.getId());

            BlockExercise blockExercise = new BlockExercise(trainingBlock, chosen, 3, 10, Intensity.MEDIUM);
            trainingBlock.addBlockExercise(blockExercise);
        }

        return trainingBlock;
    }

    private List<UserExercise> getCandidates(List<UserExercise> exercises, Set<Long> usedIds) {
        List<UserExercise> unusedExercises = exercises.stream()
                                                      .filter(ue -> !usedIds.contains(ue.getId()))
                                                      .toList();
        if (unusedExercises.isEmpty()) {
            usedIds.clear();
            return exercises;
        }

        return unusedExercises;
    }
}
