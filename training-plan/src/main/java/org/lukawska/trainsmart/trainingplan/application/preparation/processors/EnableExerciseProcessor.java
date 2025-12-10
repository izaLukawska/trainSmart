package org.lukawska.trainsmart.trainingplan.application.preparation.processors;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.lukawska.trainsmart.healthsurvey.application.service.HealthSurveyService;
import org.lukawska.trainsmart.healthsurvey.domain.entites.HealthSurvey;
import org.lukawska.trainsmart.trainingplan.application.preparation.dto.EnableExerciseStrategyContext;
import org.lukawska.trainsmart.trainingplan.application.preparation.resolvers.EnableExerciseStrategyResolver;
import org.lukawska.trainsmart.trainingplan.application.service.UserExerciseService;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class EnableExerciseProcessor {

    private final HealthSurveyService healthSurveyService;

    private final UserExerciseService userExerciseService;

    private final EnableExerciseStrategyResolver strategyResolver;

    public Map<MuscleGroup, List<UserExercise>> enableUserExercises(Long userId, Optional<Instant> lastPlanCreateDate) {
        List<String> newExerciseNames = userExerciseService.syncUserExercise(userId);

        HealthSurvey healthSurvey = healthSurveyService.getExistingHealthSurvey(userId);
        Set<String> injuries = healthSurvey.getInjuries();
        Instant updatedAt = healthSurvey.getInjuriesUpdatedAt();

        EnableExerciseStrategyContext context = buildContext(lastPlanCreateDate, newExerciseNames, injuries, updatedAt);

        strategyResolver.chooseStrategy(context)
                        .ifPresent(strategy -> strategy.updateStatus(userId, injuries, newExerciseNames));

        return userExerciseService.getEnabledUserExercisesByMuscleGroup(userId);
    }

    private EnableExerciseStrategyContext buildContext(Optional<Instant> lastCreateDate, List<String> newExerciseNames,
                                                       Set<String> injuries, Instant updatedAt) {
        boolean injuriesEmpty = injuries.isEmpty();
        boolean hasNewExercises = !newExerciseNames.isEmpty();
        boolean injuriesUpdated = injuriesUpdatedAfterDate(updatedAt, lastCreateDate);

        return new EnableExerciseStrategyContext(injuriesEmpty, injuriesUpdated, hasNewExercises);

    }

    private boolean injuriesUpdatedAfterDate(Instant injuriesUpdate, Optional<Instant> date) {
        return date.isPresent() && injuriesUpdate.isAfter(date.get());
    }
}
