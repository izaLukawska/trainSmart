package org.lukawska.trainsmart.trainingplan.application.service;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;

@UtilityClass
public class ExerciseLoadCalculator {

    public static double calculateLoadPercent(TrainingType trainingType, int reps, int sets) {
        double minPercent = trainingType.getMinPercent();
        double maxPercent = trainingType.getMaxPercent();

        int minReps = trainingType.getMinReps();
        int maxReps = trainingType.getMaxReps();

        int minSets = trainingType.getMinSets();
        int repsInReserve = trainingType.getRepsInReserve();

        double ratio = calculateRepsNormalizationRatio(reps, minReps, maxReps);
        double percent = maxPercent - (maxPercent - minPercent) * ratio;

        percent = adjustPercentForFatigue(percent, sets, minSets);
        percent = reduceRepsInReserve(percent, repsInReserve);

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
        double adjustment = 1.0 - 0.02 * (sets - minSets);
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
        return percent - 0.03 * rir;
    }
}
