package org.lukawska.trainsmart.trainingplan.domain.valueObjects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum TrainingType {

    STRENGTH(1, 5, 1, 5, IntensityLevel.HIGH),
    HYPERTROPHY(8, 12, 3, 6, IntensityLevel.MEDIUM),
    ENDURANCE(15, 25, 2, 4, IntensityLevel.LIGHT);

    private final int minReps;

    private final int maxReps;

    private final int minSets;

    private final int maxSets;

    private final IntensityLevel defaultIntensityLevel;

}
