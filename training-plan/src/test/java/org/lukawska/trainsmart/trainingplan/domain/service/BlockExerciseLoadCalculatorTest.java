package org.lukawska.trainsmart.trainingplan.domain.service;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.trainingplan.domain.entities.BlockExercise;

import static org.assertj.core.api.Assertions.assertThat;

class BlockExerciseLoadCalculatorTest {

    @Test
    void shouldReturnHalfUpRoundedDoubleValue() {
        //given
        final BlockExercise blockExercise = BlockExercise.builder()
                                                         .reps(5)
                                                         .sets(5)
                                                         .build();
        final double expectedValue = 0.77;

        //when
        double resultValue = BlockExerciseLoadCalculator.calculateLoadPercent(blockExercise);

        //then
        assertThat(resultValue).isEqualTo(expectedValue);
    }
}
