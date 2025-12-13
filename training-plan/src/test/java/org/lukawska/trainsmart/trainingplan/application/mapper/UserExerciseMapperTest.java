package org.lukawska.trainsmart.trainingplan.application.mapper;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.trainingplan.application.dto.UserExerciseResponse;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lukawska.trainsmart.trainingplan.testutil.UserExerciseTestData.userExerciseWithMockedData;
import static org.mockito.Mockito.when;

class UserExerciseMapperTest {

    private final UserExerciseMapper mapper = Mappers.getMapper(UserExerciseMapper.class);

    @Test
    void shouldMapUserExerciseToResponse() {
        //given
        final UserExercise userExercise = userExerciseWithMockedData();
        when(userExercise.getExercise().getName()).thenReturn("push up");

        //when
        UserExerciseResponse result = mapper.toResponse(userExercise);

        //then
        assertThat(result.exerciseName()).isEqualTo("push up");
    }

    @Test
    void shouldMapListOfUserExercisesToResponseList() {
        // Given
        final UserExercise userExercise1 = userExerciseWithMockedData();
        final UserExercise userExercise2 = userExerciseWithMockedData();
        when(userExercise1.getExercise().getName()).thenReturn("push up");
        when(userExercise2.getExercise().getName()).thenReturn("pull up");

        List<UserExercise> exercises = List.of(userExercise1, userExercise2);

        // When
        List<UserExerciseResponse> result = mapper.toResponseList(exercises);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).exerciseName()).isEqualTo("push up");
        assertThat(result.get(1).exerciseName()).isEqualTo("pull up");
    }
}
