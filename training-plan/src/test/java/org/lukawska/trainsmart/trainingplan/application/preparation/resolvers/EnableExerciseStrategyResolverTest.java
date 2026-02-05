package org.lukawska.trainsmart.trainingplan.application.preparation.resolvers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.trainingplan.application.preparation.dto.EnableExerciseStrategyContext;
import org.lukawska.trainsmart.trainingplan.application.preparation.strategies.EnableExerciseStrategy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnableExerciseStrategyResolverTest {

    @Test
    void shouldReturnEmptyStrategy() {
        // given
        final EnableExerciseStrategy notMatchingStrategy1 = mock(EnableExerciseStrategy.class);
        final EnableExerciseStrategy notMatchingStrategy2 = mock(EnableExerciseStrategy.class);
        final List<EnableExerciseStrategy> strategies = List.of(notMatchingStrategy1, notMatchingStrategy2);
        final EnableExerciseStrategyResolver resolver = new EnableExerciseStrategyResolver(strategies);
        final EnableExerciseStrategyContext context = new EnableExerciseStrategyContext(false, false, false);

        when(notMatchingStrategy1.matches(context)).thenReturn(false);
        when(notMatchingStrategy2.matches(context)).thenReturn(false);

        // when
        Optional<EnableExerciseStrategy> result = resolver.chooseStrategy(context);

        // then
        assertThat(result.isPresent()).isFalse();
    }

    @Test
    void shouldReturnStrategy() {
        // given
        final EnableExerciseStrategy notMatchingStrategy = mock(EnableExerciseStrategy.class);
        final EnableExerciseStrategy matchingStrategy = mock(EnableExerciseStrategy.class);
        final List<EnableExerciseStrategy> strategies = List.of(notMatchingStrategy, matchingStrategy);
        final EnableExerciseStrategyResolver resolver = new EnableExerciseStrategyResolver(strategies);
        final EnableExerciseStrategyContext context = new EnableExerciseStrategyContext(true, true, false);

        when(notMatchingStrategy.matches(context)).thenReturn(false);
        when(matchingStrategy.matches(context)).thenReturn(true);

        // when
        Optional<EnableExerciseStrategy> result = resolver.chooseStrategy(context);

        // then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(matchingStrategy);
        assertThat(result.get()).isNotEqualTo(notMatchingStrategy);
    }
}
