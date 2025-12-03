package org.lukawska.trainsmart.trainingplan.application.service.strategy;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.healthsurvey.application.service.HealthSurveyService;
import org.lukawska.trainsmart.trainingplan.application.service.UserExerciseFilter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class InjuriesNotEmptyWithNewExercises implements EnableExerciseStrategy {

    private final UserExerciseFilter userExerciseFilter;

    private final HealthSurveyService healthSurveyService;

    @Override
    public boolean matches(EnableExerciseStrategyContext context) {
        return !context.injuriesUpdated() && !context.injuriesEmpty() && context.hasNewExercises();
    }

    @Override
    public void updateStatus(Long userId, List<String> newExerciseNames) {
        Set<String> injuries = healthSurveyService.getAllInjuriesByUserId(userId);
        userExerciseFilter.updateUnsafeExercises(userId, injuries, newExerciseNames);
    }
}
