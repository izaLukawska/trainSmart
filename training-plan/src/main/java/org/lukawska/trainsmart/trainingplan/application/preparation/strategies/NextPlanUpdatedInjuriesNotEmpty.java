package org.lukawska.trainsmart.trainingplan.application.preparation.strategies;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.trainingplan.application.preparation.dto.EnableExerciseStrategyContext;
import org.lukawska.trainsmart.trainingplan.application.preparation.processors.UserExerciseFilter;
import org.lukawska.trainsmart.trainingplan.application.service.UserExerciseService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class NextPlanUpdatedInjuriesNotEmpty implements EnableExerciseStrategy {

    private final UserExerciseService userExerciseService;

    private final UserExerciseFilter userExerciseFilter;

    @Override
    public boolean matches(EnableExerciseStrategyContext context) {
        return context.injuriesUpdated() && !context.injuriesEmpty();
    }

    @Override
    public void updateStatus(Long userId, Set<String> injuries, List<String> newExerciseNames) {
        List<String> allExerciseNames = userExerciseService.getAllExerciseNames(userId);
        userExerciseFilter.updateUnsafeExercises(userId, injuries, allExerciseNames);
    }
}
