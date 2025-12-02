package org.lukawska.trainsmart.trainingplan.application.service;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.trainingplan.application.exception.ExceptionType;
import org.lukawska.trainsmart.trainingplan.application.exception.TrainingPlanException;
import org.lukawska.trainsmart.trainingplan.application.service.strategy.UserExerciseFilterStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserExerciseStrategyResolver {

    private final List<UserExerciseFilterStrategy> strategies;

    public UserExerciseFilterStrategy chooseStrategy(UserExerciseContext context) {
        return strategies.stream()
                         .filter(strategy -> strategy.matches(context))
                         .findFirst()
                         .orElseThrow(() -> new TrainingPlanException(ExceptionType.NO_FILTER_STRATEGY_MATCH));
    }
}
