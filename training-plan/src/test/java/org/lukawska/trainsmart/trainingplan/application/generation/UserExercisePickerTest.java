package org.lukawska.trainsmart.trainingplan.application.generation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lukawska.trainsmart.trainingplan.application.generation.UserExercisePicker.pickExercise;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserExercisePickerTest {

    @Test
    void shouldPickRandomExerciseWhenAllExercisesUsed() {
        //given
        final UserExercise userExercise = mock(UserExercise.class);
        final List<UserExercise> userExercises = List.of(userExercise);
        final Set<UserExercise> usedExercises = Set.of(userExercise);

        //when
        UserExercise result = pickExercise(userExercises, usedExercises);

        //then
        assertThat(result).isEqualTo(userExercise);
    }

    @Test
    void shouldPickNeverPerformedExerciseWhenSuchExerciseExists() {
        //given
        final UserExercise neverUsedExercise = mock(UserExercise.class);
        final UserExercise usedExercise = mock(UserExercise.class);
        final List<UserExercise> exercises = List.of(neverUsedExercise, usedExercise);

        when(neverUsedExercise.getLastUsedAt()).thenReturn(null);
        //when
        UserExercise result = pickExercise(exercises, Set.of());

        //then
        assertThat(result).isEqualTo(neverUsedExercise);
    }

    @Test
    void shouldPickExerciseNotUsedInPlan() {
        //given
        final UserExercise usedInPlanExercise = mock(UserExercise.class);
        final UserExercise notUsedInPlanExercise = mock(UserExercise.class);
        final List<UserExercise> exercises = List.of(usedInPlanExercise, notUsedInPlanExercise);
        final Set<UserExercise> usedExercises = Set.of(usedInPlanExercise);

        when(notUsedInPlanExercise.getLastUsedAt()).thenReturn(Instant.now());

        //when
        UserExercise result = pickExercise(exercises, usedExercises);

        //then
        assertThat(result).isEqualTo(notUsedInPlanExercise);
    }
}
