package org.lukawska.trainsmart.trainingplan.application.service.strategy;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.lukawska.trainsmart.trainingplan.application.service.UserExerciseContext;
import org.lukawska.trainsmart.trainingplan.application.service.UserExerciseService;
import org.lukawska.trainsmart.trainingplan.domain.entity.UserExercise;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NoInjuriesStrategy implements UserExerciseFilterStrategy {

    private final UserExerciseService userExerciseService;

    @Override
    public boolean matches(UserExerciseContext context) {
        return context.injuries().isEmpty();
    }

    @Override
    public Map<MuscleGroup, List<UserExercise>> filterExercises(Long userId, UserExerciseContext context) {
        if (context.injuriesChanged()) {
            userExerciseService.updateUserExerciseEnabledStatus(userId, List.of());
        }

        return userExerciseService.getEnabledUserExercisesByMuscleGroup(userId);
    }
}
