package org.lukawska.trainsmart.trainingplan.application.generation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.lukawska.trainsmart.trainingplan.application.preparation.dto.TrainingPlanGenerationData;
import org.lukawska.trainsmart.trainingplan.domain.entities.*;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.IntensityLevel;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.WeekDay;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.lukawska.trainsmart.trainingplan.application.generation.UserExercisePicker.pickExercise;
import static org.lukawska.trainsmart.trainingplan.application.mapper.TrainingPlanMapper.mapToTrainingPlan;
import static org.lukawska.trainsmart.trainingplan.domain.service.BlockExerciseLoadCalculator.calculateLoadPercent;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingPlanGenerator {

    public TrainingPlan generateTrainingPlan(TrainingPlanGenerationData generationData) {
        TrainingPlan trainingPlan = mapToTrainingPlan(generationData);
        TrainingType trainingType = generationData.trainingType();
        int weekCount = generationData.planDuration().getWeeksCount();
        int daysPerWeek = generationData.preferredDays().size();

        log.info("Generating {} plan for {} weeks and {} days per week.", trainingType.name(), weekCount, daysPerWeek);

        Set<UserExercise> usedExercises = new HashSet<>();

        for (int week = 1; week <= weekCount; week++) {
            log.debug("Generating {} week for training plan.", week);
            TrainingWeek trainingWeek = new TrainingWeek(trainingPlan, week);

            for (int day = 1; day <= daysPerWeek; day++) {
                WeekDay scheduledDay = generationData.preferredDays().get(day - 1);
                TrainingBlock trainingBlock = generateTrainingBlock(
                        trainingWeek, generationData.muscleGroups(), usedExercises, trainingType, scheduledDay);
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
        for (Map.Entry<MuscleGroup, List<UserExercise>> entry : groups.entrySet()) {
            List<UserExercise> exercises = entry.getValue();

            UserExercise pickedExercise = pickExercise(exercises, usedExercises);
            pickedExercise.recordExerciseUse();
            usedExercises.add(pickedExercise);

            log.debug("Creating block exercise with picked user exercise {}", pickedExercise.getId());
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

        Double load = userExercise.isBarbellExercise() ? calculateLoadPercent(blockExercise) : null;
        blockExercise.setLoadPercent(load);

        log.debug("Generated exercise block with reps: {}, sets: {}, intensity: {}, load: {}",
                  reps, sets, intensityLevel.name(), load);

        return blockExercise;
    }

    private int getExerciseReps(TrainingType trainingType) {
        return ThreadLocalRandom.current().nextInt(trainingType.getMinReps(), trainingType.getMaxReps() + 1);
    }

    private int getExerciseSets(TrainingType trainingType) {
        return ThreadLocalRandom.current().nextInt(trainingType.getMinSets(), trainingType.getMaxSets() + 1);
    }
}
