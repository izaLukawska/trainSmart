package org.lukawska.trainsmart.trainingplan.application.preparation.resolvers;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanRequest;
import org.lukawska.trainsmart.trainingplan.application.preparation.dto.TrainingPlanGenerationData;
import org.lukawska.trainsmart.trainingplan.application.preparation.processors.EnableExerciseProcessor;
import org.lukawska.trainsmart.trainingplan.domain.entity.UserExercise;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.WeekDay;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.lukawska.trainsmart.trainingplan.application.preparation.resolvers.PreferredDaysResolver.resolvePreferredDays;
import static org.lukawska.trainsmart.trainingplan.application.preparation.validation.UserExerciseValidator.validateUserExercises;

@Component
@RequiredArgsConstructor
public class TrainingPlanDataResolver {

    private final EnableExerciseProcessor enableExerciseProcessor;

    public TrainingPlanGenerationData getResolvedData(User user, TrainingPlanRequest request,
                                                      Optional<Instant> lastPlanCreatedAt) {
        List<WeekDay> sortedDays = resolvePreferredDays(request.preferredDays(), request.daysPerWeek());
        Map<MuscleGroup, List<UserExercise>> muscleGroups = enableExerciseProcessor.enableUserExercises(
                user.getId(), lastPlanCreatedAt);
        validateUserExercises(muscleGroups);

        return new TrainingPlanGenerationData(
                user, muscleGroups, request.trainingType(), request.planDuration(), sortedDays);
    }
}
