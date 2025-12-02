package org.lukawska.trainsmart.trainingplan.application.service.strategy;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.lukawska.trainsmart.trainingplan.application.service.UserExerciseContext;
import org.lukawska.trainsmart.trainingplan.application.service.UserExerciseFilter;
import org.lukawska.trainsmart.trainingplan.application.service.UserExerciseService;
import org.lukawska.trainsmart.trainingplan.domain.entity.UserExercise;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NextPlanInjuriesChangedStrategy implements UserExerciseFilterStrategy {

    private final UserExerciseService userExerciseService;

    private final UserExerciseFilter userExerciseFilter;

    @Override
    public boolean matches(UserExerciseContext context) {
        return !context.injuries().isEmpty() && context.injuriesChanged();
    }

    @Override
    public Map<MuscleGroup, List<UserExercise>> filterExercises(Long userId, UserExerciseContext context) {
        List<String> allExerciseNames = userExerciseService.getAllExerciseNames(userId);
        userExerciseFilter.updateUnsafeExercises(userId, context.injuries(), allExerciseNames);

        return userExerciseService.getEnabledUserExercisesByMuscleGroup(userId);
    }
}
