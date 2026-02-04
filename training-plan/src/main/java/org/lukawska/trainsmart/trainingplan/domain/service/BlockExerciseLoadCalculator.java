package org.lukawska.trainsmart.trainingplan.domain.service;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.trainingplan.domain.entities.BlockExercise;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class BlockExerciseLoadCalculator {

    private static final double FATIGUE_ADJUSTMENT = 0.03;

    /**
     * Calculates the load factor for a {@code BlockExercise}, reducing it according to cumulative fatigue.
     *
     * @param blockExercise Object providing repetition and sets.
     * @return Load reduced by the percent of cumulative fatigue curve (rounded to 2 decimal places).
     */
    public static double calculateLoadPercent(BlockExercise blockExercise) {
        double basePercent = 1 / (1 + FATIGUE_ADJUSTMENT * (blockExercise.getReps() - 1));
        double fatigue = FATIGUE_ADJUSTMENT * (blockExercise.getSets() - 1);

        return BigDecimal.valueOf(basePercent - fatigue).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
