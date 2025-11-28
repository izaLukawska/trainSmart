package org.lukawska.trainsmart.trainingplan.application.service;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.lukawska.trainsmart.sharedpersistence.application.service.UserService;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanRequest;
import org.lukawska.trainsmart.trainingplan.domain.entity.*;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.IntensityLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

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

        Set<UserExercise> usedExercises = new HashSet<>();

        for (int week = 1; week <= request.duration().getWeeksCount(); week++) {
            TrainingWeek trainingWeek = new TrainingWeek(trainingPlan, week);
            for (int day = 1; day <= request.daysPerWeek(); day++) {
                TrainingBlock trainingBlock = generateTrainingBlock(trainingWeek, groups, usedExercises);
                trainingWeek.addTrainingBlock(trainingBlock);
            }
        }

        return trainingPlan;
    }

    private TrainingBlock generateTrainingBlock(TrainingWeek trainingWeek,
                                                Map<MuscleGroup, List<UserExercise>> groups,
                                                Set<UserExercise> usedExercises) {
        TrainingBlock trainingBlock = new TrainingBlock(trainingWeek);
        for (MuscleGroup muscleGroup : groups.keySet()) {
            List<UserExercise> exercises = groups.get(muscleGroup);

            UserExercise pickedExercise = pickExercise(exercises, usedExercises);
            pickedExercise.recordExerciseUse();
            usedExercises.add(pickedExercise);

            BlockExercise blockExercise = new BlockExercise(trainingBlock, pickedExercise, 5, 5, IntensityLevel.HEAVY);
            trainingBlock.addBlockExercise(blockExercise);
        }

        return trainingBlock;
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

    private TrainingPlan mapToTrainingPlan(TrainingPlanRequest request) {
        return new TrainingPlan(userService.getUserById(request.userId()),
                                request.trainingType(),
                                request.duration(),
                                request.daysPerWeek());
    }
}
