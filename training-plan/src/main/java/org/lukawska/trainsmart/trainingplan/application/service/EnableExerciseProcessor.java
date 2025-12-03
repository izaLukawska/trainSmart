package org.lukawska.trainsmart.trainingplan.application.service;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.lukawska.trainsmart.healthsurvey.application.service.HealthSurveyService;
import org.lukawska.trainsmart.healthsurvey.domain.entites.HealthSurvey;
import org.lukawska.trainsmart.trainingplan.application.service.strategy.EnableExerciseStrategyContext;
import org.lukawska.trainsmart.trainingplan.application.service.strategy.EnableExerciseStrategyResolver;
import org.lukawska.trainsmart.trainingplan.domain.entity.UserExercise;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EnableExerciseProcessor {

    private final HealthSurveyService healthSurveyService;

    private final UserExerciseService userExerciseService;

    private final EnableExerciseStrategyResolver strategyResolver;

    public Map<MuscleGroup, List<UserExercise>> enableUserExercises(Long userId, Optional<Instant> lastCreateDate) {
        HealthSurvey healthSurvey = healthSurveyService.getExistingHealthSurvey(userId);
        List<String> newExerciseNames = userExerciseService.syncUserExercise(userId);
        EnableExerciseStrategyContext context = buildContext(healthSurvey, newExerciseNames, lastCreateDate);

        strategyResolver.chooseStrategy(context)
                        .ifPresent(strategy -> strategy.updateStatus(userId, newExerciseNames));

        return userExerciseService.getEnabledUserExercisesByMuscleGroup(userId);
    }

    private EnableExerciseStrategyContext buildContext(HealthSurvey healthSurvey, List<String> newExerciseNames,
                                                       Optional<Instant> lastCreateDate) {
        boolean injuriesEmpty = healthSurvey.getInjuries().isEmpty();
        boolean hasNewExercises = !newExerciseNames.isEmpty();
        boolean injuriesUpdated = injuriesUpdatedAfterDate(healthSurvey.getInjuriesUpdatedAt(), lastCreateDate);

        return new EnableExerciseStrategyContext(injuriesEmpty, injuriesUpdated, hasNewExercises);

    }

    private boolean injuriesUpdatedAfterDate(Instant injuriesUpdate, Optional<Instant> date) {
        return date.isPresent() && injuriesUpdate.isAfter(date.get());
    }
}
