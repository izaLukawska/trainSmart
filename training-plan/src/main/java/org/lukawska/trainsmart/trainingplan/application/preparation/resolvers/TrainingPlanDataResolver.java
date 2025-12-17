package org.lukawska.trainsmart.trainingplan.application.preparation.resolvers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.exercisecatalog.domain.valueObject.MuscleGroup;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanDto;
import org.lukawska.trainsmart.trainingplan.application.preparation.dto.TrainingPlanGenerationData;
import org.lukawska.trainsmart.trainingplan.application.preparation.processors.EnableExerciseProcessor;
import org.lukawska.trainsmart.trainingplan.domain.entities.UserExercise;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.WeekDay;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

import static org.lukawska.trainsmart.trainingplan.application.preparation.validation.UserExerciseValidator.validateUserExercises;

@Component
@RequiredArgsConstructor
@Slf4j
public class TrainingPlanDataResolver {

    private final EnableExerciseProcessor enableExerciseProcessor;

    public TrainingPlanGenerationData getResolvedData(User user, TrainingPlanDto request,
                                                      Optional<Instant> lastPlanCreatedAt) {
        log.debug("Preparing data for training plan generation");
        List<WeekDay> sortedDays = resolvePreferredDays(request.preferredDays(), request.daysPerWeek());
        Map<MuscleGroup, List<UserExercise>> groups = enableExerciseProcessor.enableUserExercises(
                user.getId(), lastPlanCreatedAt);
        validateUserExercises(groups);

        return new TrainingPlanGenerationData(user, groups, request.trainingType(), request.planDuration(), sortedDays);
    }

    public List<WeekDay> resolvePreferredDays(List<WeekDay> weekDays, int daysPerWeek) {
        if (weekDays.isEmpty()) {
            log.debug("No preferred days specified. Choosing default training days schedule");
            return getDefaultDays(daysPerWeek);
        } else {
            log.debug("Preferred workout days found.");
            return weekDays.stream()
                           .sorted(Comparator.comparing(Enum::ordinal))
                           .toList();
        }
    }

    private List<WeekDay> getDefaultDays(int daysPerWeek) {
        return switch (daysPerWeek) {
            case 1 -> List.of(WeekDay.MONDAY);
            case 2 -> List.of(WeekDay.MONDAY, WeekDay.THURSDAY);
            case 3 -> List.of(WeekDay.MONDAY, WeekDay.WEDNESDAY, WeekDay.FRIDAY);
            case 4 -> List.of(WeekDay.MONDAY, WeekDay.TUESDAY, WeekDay.THURSDAY, WeekDay.FRIDAY);
            case 5 -> List.of(WeekDay.MONDAY, WeekDay.TUESDAY, WeekDay.WEDNESDAY, WeekDay.THURSDAY, WeekDay.FRIDAY);
            case 6 -> List.of(WeekDay.MONDAY, WeekDay.TUESDAY, WeekDay.WEDNESDAY, WeekDay.THURSDAY, WeekDay.FRIDAY,
                              WeekDay.SATURDAY);
            default -> Arrays.stream(WeekDay.values()).toList();
        };
    }
}
