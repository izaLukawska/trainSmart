package org.lukawska.trainsmart.trainingplan.domain.valueObjects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Duration {

    FOUR_WEEKS(4), EIGHT_WEEKS(8), TWELVE_WEEKS(12);

    private final int weeksCount;

}
