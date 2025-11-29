package org.lukawska.trainsmart.trainingplan.domain.service;

import org.lukawska.trainsmart.trainingplan.domain.entity.BlockExercise;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class BlockExerciseLoadCalculator {

    private static final double FATIGUE_ADJUSTMENT_RATE = 0.02;

    private static final double RIR_PERCENTAGE_REDUCTION = 0.03;

    /**
     * Calculates the target load percentage of 1RM for the given block exercise and training type.
     * Algorithm:
     * 1. Interpolates between {@code trainingType.getMaxPercent()} and {@code trainingType.getMinPercent()}
     * based on the number of repetitions within {@code minReps..maxReps}.
     * 2. Adjusts the interpolated percentage for fatigue: the percentage is reduced proportionally
     * to extra sets beyond {@code minSets} using {@code FATIGUE_ADJUSTMENT_RATE}.
     * 3. Applies a linear reduction based on Reps In Reserve (RIR) using
     * {@code RIR_PERCENTAGE_REDUCTION} per RIR step.
     * 4. Clamps the final value to the range {@code [minPercent, maxPercent]}.
     *
     * @param blockExercise Object containing data  (e.g. reps, sets) for load calculation.
     * @param trainingType  Object containing data (e.g. intensity level) for specific training type.
     * @return Calculated percent for BlockExercise.
     */
    public double calculateLoadPercent(BlockExercise blockExercise, TrainingType trainingType) {
        double minPercent = trainingType.getMinPercent();
        double maxPercent = trainingType.getMaxPercent();

        int minReps = trainingType.getMinReps();
        int maxReps = trainingType.getMaxReps();

        int minSets = trainingType.getMinSets();
        int repsInReserve = trainingType.getRepsInReserve();

        double ratio = calculateRepsNormalizationRatio(blockExercise.getReps(), minReps, maxReps);
        double percent = maxPercent - (maxPercent - minPercent) * ratio;

        percent = adjustPercentForFatigue(percent, blockExercise.getSets(), minSets);
        percent = reduceRepsInReserve(percent, repsInReserve);
        percent = BigDecimal.valueOf(percent).setScale(2, RoundingMode.HALF_DOWN).doubleValue();

        return Math.max(minPercent, Math.min(maxPercent, percent));
    }

    /**
     * Calculates the normalization ratio of the current repetition count within the defined range
     * for a specific training type.
     * This ratio is used as the interpolation factor when determining the percentage
     * of 1RM (One Repetition Maximum) loading.
     * The returned value is between 0.0 (highest load) and 1.0 (lowest load):
     *
     * @param reps    The actual number of repetitions performed in the set.
     * @param minReps The minimum number of repetitions expected for the training type's range.
     * @param maxReps The maximum number of repetitions expected for the training type's range.
     * @return The calculated normalization ratio (interpolation factor) between 0.0 and 1.0.
     */
    private double calculateRepsNormalizationRatio(int reps, int minReps, int maxReps) {
        return (double) (reps - minReps) / (maxReps - minReps);
    }

    /**
     * Custom logic for adjusting given percent base on fatigue.
     * The more sets performed, the lower the adjusted percent.
     *
     * @param percent Current calculated percent of 1RM (one-repetition maximum).
     * @param sets    current sets count
     * @param minSets minimum sets to perform exercise
     * @return adjusted percent accounting for fatigue
     */
    private double adjustPercentForFatigue(double percent, int sets, int minSets) {
        double adjustment = 1.0 - FATIGUE_ADJUSTMENT_RATE * (sets - minSets);
        return percent * adjustment;
    }

    /**
     * Applies a linear reduction to the current percentage based on Reps In Reserve (RIR).
     * Assumes a constant 3% reduction per 1 RIR step.
     *
     * @param percent Current calculated percent of 1RM.
     * @param rir     Reps in Reserve
     * @return The percentage value after RIR-based reduction.
     */
    private double reduceRepsInReserve(double percent, int rir) {
        return percent - RIR_PERCENTAGE_REDUCTION * rir;
    }
}
