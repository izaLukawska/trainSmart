package org.lukawska.trainsmart.trainingplan.application.preparation.strategies;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.trainingplan.application.preparation.dto.EnableExerciseStrategyContext;
import org.lukawska.trainsmart.trainingplan.application.preparation.processors.UserExerciseFilter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class InjuriesNotEmptyWithNewExercises implements EnableExerciseStrategy {

    private final UserExerciseFilter userExerciseFilter;

    @Override
    public boolean matches(EnableExerciseStrategyContext context) {
        return !context.injuriesUpdated() && !context.injuriesEmpty() && context.hasNewExercises();
    }

    @Override
    public void updateStatus(Long userId, Set<String> injuries, List<String> newExerciseNames) {
        userExerciseFilter.updateUnsafeExercises(userId, injuries, newExerciseNames);
    }
}
