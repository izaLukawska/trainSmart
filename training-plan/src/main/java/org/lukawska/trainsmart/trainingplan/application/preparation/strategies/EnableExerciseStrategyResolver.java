package org.lukawska.trainsmart.trainingplan.application.preparation.strategies;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.trainingplan.application.preparation.dto.EnableExerciseStrategyContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EnableExerciseStrategyResolver {

    private final List<EnableExerciseStrategy> strategies;

    public Optional<EnableExerciseStrategy> chooseStrategy(EnableExerciseStrategyContext context) {
        return strategies.stream()
                         .filter(strategy -> strategy.matches(context))
                         .findFirst();
    }
}
