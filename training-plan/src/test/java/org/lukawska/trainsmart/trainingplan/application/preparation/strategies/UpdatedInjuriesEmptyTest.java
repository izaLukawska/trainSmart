package org.lukawska.trainsmart.trainingplan.application.preparation.strategies;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.trainingplan.application.preparation.dto.EnableExerciseStrategyContext;
import org.lukawska.trainsmart.trainingplan.application.service.UserExerciseService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UpdatedInjuriesEmptyTest {

    @Mock
    private UserExerciseService userExerciseService;

    @InjectMocks
    private UpdatedInjuriesEmpty updatedInjuriesEmpty;

    @Test
    void shouldReturnTrueWhenContextMatches() {
        //given
        final EnableExerciseStrategyContext context = new EnableExerciseStrategyContext(true, true, false);

        //when
        boolean matches = updatedInjuriesEmpty.matches(context);

        //then
        assertThat(matches).isTrue();
    }

    @Test
    void shouldReturnFalseWhenContextMatches() {
        //given
        final EnableExerciseStrategyContext context = new EnableExerciseStrategyContext(false, false, true);

        //when
        boolean matches = updatedInjuriesEmpty.matches(context);

        //then
        assertThat(matches).isFalse();
    }

    @Test
    void shouldDelegateUpdateStatusToUserService() {
        //given
        final Long userId = 1L;
        final Set<String> injuries = Set.of("knee");
        final List<String> newExercises = List.of("Squat");

        //when
        updatedInjuriesEmpty.updateStatus(userId, injuries, newExercises);

        //then
        verify(userExerciseService).updateUserExerciseEnabledStatus(userId, List.of());
    }
}
