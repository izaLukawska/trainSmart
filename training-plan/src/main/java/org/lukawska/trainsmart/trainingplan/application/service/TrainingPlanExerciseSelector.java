package org.lukawska.trainsmart.trainingplan.application.service;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.lukawska.trainsmart.healthsurvey.application.service.HealthSurveyService;
import org.lukawska.trainsmart.healthsurvey.domain.entites.HealthSurvey;
import org.lukawska.trainsmart.trainingplan.application.service.strategy.UserExerciseFilterStrategy;
import org.lukawska.trainsmart.trainingplan.domain.entity.UserExercise;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TrainingPlanExerciseSelector {

    private final UserExerciseService userExerciseService;

    private final HealthSurveyService healthSurveyService;

    private final UserExerciseStrategyResolver strategyResolver;

    private final TrainingPlanService trainingPlanService;

    public Map<MuscleGroup, List<UserExercise>> selectExercises(Long userId) {
        UserExerciseContext context = buildContext(userId);

        UserExerciseFilterStrategy strategy = strategyResolver.chooseStrategy(context);
        return strategy.filterExercises(userId, context);
    }

    private UserExerciseContext buildContext(Long userId) {
        HealthSurvey healthSurvey = healthSurveyService.getExistingHealthSurvey(userId);
        boolean injuriesChanged = checkIfInjuriesChanged(userId, healthSurvey);
        List<String> newExercises = userExerciseService.syncUserExercise(userId);

        return new UserExerciseContext(healthSurvey.getInjuries(), injuriesChanged, newExercises);
    }

    private boolean checkIfInjuriesChanged(Long userId, HealthSurvey survey) {
        Optional<Instant> lastCreationDate = trainingPlanService.getLastPlanCreationDate(userId);
        return lastCreationDate.isPresent() && survey.getInjuriesUpdatedAt().isAfter(lastCreationDate.get());
    }
}
