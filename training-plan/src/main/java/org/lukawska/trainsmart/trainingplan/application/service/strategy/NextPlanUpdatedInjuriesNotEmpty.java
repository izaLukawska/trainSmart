package org.lukawska.trainsmart.trainingplan.application.service.strategy;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.healthsurvey.application.service.HealthSurveyService;
import org.lukawska.trainsmart.trainingplan.application.service.UserExerciseFilter;
import org.lukawska.trainsmart.trainingplan.application.service.UserExerciseService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class NextPlanUpdatedInjuriesNotEmpty implements EnableExerciseStrategy {

    private final UserExerciseService userExerciseService;

    private final UserExerciseFilter userExerciseFilter;

    private final HealthSurveyService healthSurveyService;

    @Override
    public boolean matches(EnableExerciseStrategyContext context) {
        return context.injuriesUpdated() && !context.injuriesEmpty();
    }

    @Override
    public void updateStatus(Long userId, List<String> newExerciseNames) {
        List<String> allExerciseNames = userExerciseService.getAllExerciseNames(userId);
        Set<String> injuries = healthSurveyService.getAllInjuriesByUserId(userId);

        userExerciseFilter.updateUnsafeExercises(userId, injuries, allExerciseNames);
    }
}
