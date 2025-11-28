package org.lukawska.trainsmart.trainingplan.domain.valueObjects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum TrainingType {

    STRENGTH(0.70, 1.00, 1, 5, 1, 5, 1),
    HYPERTROPHY(0.60, 0.80, 8, 12, 3, 6, 2),
    ENDURANCE(0.40, 0.60, 15, 25, 2, 4, 3);

    private final double minPercent;

    private final double maxPercent;

    private final int minReps;

    private final int maxReps;

    private final int minSets;
    
    private final int maxSets;

    private final int repsInReserve;

}
