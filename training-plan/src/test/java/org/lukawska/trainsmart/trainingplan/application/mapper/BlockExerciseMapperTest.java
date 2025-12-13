package org.lukawska.trainsmart.trainingplan.application.mapper;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.exercisecatalog.domain.entity.Exercise;
import org.lukawska.trainsmart.trainingplan.application.dto.BlockExerciseResponse;
import org.lukawska.trainsmart.trainingplan.domain.entities.BlockExercise;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingBlock;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.IntensityLevel;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BlockExerciseMapperTest {

    private final BlockExerciseMapper blockExerciseMapper = Mappers.getMapper(BlockExerciseMapper.class);

    @Test
    void shouldMapTrainingWeekToResponse() {
        //given
        final String expectedName = "pull up";
        final UserExercise userExercise = mock(UserExercise.class);
        final Exercise exercise = mock(Exercise.class);
        final BlockExercise blockExercise = BlockExercise.builder()
                                                         .trainingBlock(mock(TrainingBlock.class))
                                                         .userExercise(userExercise)
                                                         .sets(5)
                                                         .reps(4)
                                                         .intensity(IntensityLevel.HIGH)
                                                         .loadPercent(0.87)
                                                         .build();
        when(userExercise.getExercise()).thenReturn(exercise);
        when(exercise.getName()).thenReturn(expectedName);

        //when
        BlockExerciseResponse result = blockExerciseMapper.toResponse(blockExercise);

        //then
        assertThat(result.reps()).isEqualTo(blockExercise.getReps());
        assertThat(result.sets()).isEqualTo(blockExercise.getSets());
        assertThat(result.loadPercent()).isEqualTo(blockExercise.getLoadPercent());
        assertThat(result.intensity()).isEqualTo(blockExercise.getIntensity());
        assertThat(result.exerciseName()).isEqualTo(expectedName);
    }
}
