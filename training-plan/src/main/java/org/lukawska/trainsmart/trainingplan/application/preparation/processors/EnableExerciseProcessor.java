package org.lukawska.trainsmart.trainingplan.application.preparation.processors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class EnableExerciseProcessor {

    private final HealthSurveyService healthSurveyService;

    private final UserExerciseService userExerciseService;

    private final EnableExerciseStrategyResolver strategyResolver;

    public Map<MuscleGroup, List<UserExercise>> enableUserExercises(Long userId, Optional<Instant> lastPlanCreateDate) {
        log.debug("Resolving the exercises status.");
        List<String> newExerciseNames = userExerciseService.syncUserExercise(userId);
        HealthSurvey healthSurvey = healthSurveyService.getExistingHealthSurvey(userId);
        Set<String> injuries = healthSurvey.getInjuries();
        Instant updatedAt = healthSurvey.getInjuriesUpdatedAt();

        EnableExerciseStrategyContext context = buildContext(lastPlanCreateDate, newExerciseNames, injuries, updatedAt);

        strategyResolver.chooseStrategy(context)
                        .ifPresentOrElse(strategy -> {
                            log.debug("Applying strategy: {}", strategy.getClass().getSimpleName());
                            strategy.updateStatus(userId, injuries, newExerciseNames);
                        }, () -> log.debug("Skipping strategy (no strategy match found)"));

        return userExerciseService.getEnabledUserExercisesByMuscleGroup(userId);
    }

    private EnableExerciseStrategyContext buildContext(Optional<Instant> lastCreateDate, List<String> newExerciseNames,
                                                       Set<String> injuries, Instant injuriesUpdatedAt) {
        boolean injuriesEmpty = injuries.isEmpty();
        boolean hasNewExercises = !newExerciseNames.isEmpty();
        boolean injuriesUpdated = lastCreateDate.isPresent() && injuriesUpdatedAt.isAfter(lastCreateDate.get());

        return new EnableExerciseStrategyContext(injuriesEmpty, injuriesUpdated, hasNewExercises);

    }
}
