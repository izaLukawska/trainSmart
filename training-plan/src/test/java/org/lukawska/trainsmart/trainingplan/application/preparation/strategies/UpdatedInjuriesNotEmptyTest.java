package org.lukawska.trainsmart.trainingplan.application.preparation.strategies;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.trainingplan.application.preparation.dto.EnableExerciseStrategyContext;
import org.lukawska.trainsmart.trainingplan.application.preparation.processors.UserExerciseFilter;
import org.lukawska.trainsmart.trainingplan.application.service.UserExerciseService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdatedInjuriesNotEmptyTest {

    @Mock
    private UserExerciseService userExerciseService;

    @Mock
    private UserExerciseFilter userExerciseFilter;

    @InjectMocks
    private UpdatedInjuriesNotEmpty updatedInjuriesNotEmpty;

    @Test
    void shouldReturnTrueWhenContextMatches() {
        //given
        final EnableExerciseStrategyContext context = new EnableExerciseStrategyContext(false, true, false);

        //when
        boolean matches = updatedInjuriesNotEmpty.matches(context);

        //then
        assertThat(matches).isTrue();
    }

    @Test
    void shouldReturnFalseWhenContextMatches() {
        //given
        final EnableExerciseStrategyContext context = new EnableExerciseStrategyContext(false, false, true);

        //when
        boolean matches = updatedInjuriesNotEmpty.matches(context);

        //then
        assertThat(matches).isFalse();
    }

    @Test
    void shouldDelegateUpdateStatusToUserExerciseFilterWithAllUserExerciseNames() {
        //given
        final Long userId = 1L;
        final Set<String> injuries = Set.of("knee");
        final List<String> newExercises = List.of("Squat");
        when(userExerciseService.getAllExerciseNames(userId)).thenReturn(newExercises);

        //when
        updatedInjuriesNotEmpty.updateStatus(userId, injuries, newExercises);

        //then
        verify(userExerciseFilter).updateUnsafeExercises(userId, injuries, newExercises);
    }
}
