package org.lukawska.trainsmart.trainingplan.application.preparation.strategies;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.trainingplan.application.preparation.dto.EnableExerciseStrategyContext;
import org.lukawska.trainsmart.trainingplan.application.preparation.processors.UserExerciseFilter;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotUpdatedInjuriesNotEmptyTest {

    @Mock
    private UserExerciseFilter userExerciseFilter;

    @InjectMocks
    private NotUpdatedInjuriesNotEmpty notUpdatedInjuriesNotEmpty;

    @Test
    void shouldReturnTrueWhenContextMatches() {
        //given
        final EnableExerciseStrategyContext context = new EnableExerciseStrategyContext(false, false, true);

        //when
        boolean matches = notUpdatedInjuriesNotEmpty.matches(context);

        //then
        assertThat(matches).isTrue();
    }

    @Test
    void shouldReturnFalseWhenContextMatches() {
        //given
        final EnableExerciseStrategyContext context = new EnableExerciseStrategyContext(true, true, false);

        //when
        boolean matches = notUpdatedInjuriesNotEmpty.matches(context);

        //then
        assertThat(matches).isFalse();
    }

    @Test
    void shouldDelegateUpdateStatusToFilter() {
        //given
        final Long userId = 1L;
        final Set<String> injuries = Set.of("knee");
        final List<String> newExercises = List.of("Squat");

        //when
        notUpdatedInjuriesNotEmpty.updateStatus(userId, injuries, newExercises);

        //then
        verify(userExerciseFilter).updateUnsafeExercises(userId, injuries, newExercises);
    }
}
