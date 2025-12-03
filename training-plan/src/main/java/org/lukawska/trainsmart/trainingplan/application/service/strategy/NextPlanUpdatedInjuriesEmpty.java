package org.lukawska.trainsmart.trainingplan.application.service.strategy;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.trainingplan.application.service.UserExerciseService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class NextPlanUpdatedInjuriesEmpty implements EnableExerciseStrategy {

    private final UserExerciseService userExerciseService;

    @Override
    public boolean matches(EnableExerciseStrategyContext context) {
        return context.injuriesUpdated() && context.injuriesEmpty();
    }

    @Override
    public void updateStatus(Long userId, Set<String> injuries, List<String> newExerciseNames) {
        userExerciseService.updateUserExerciseEnabledStatus(userId, List.of());
    }
}
