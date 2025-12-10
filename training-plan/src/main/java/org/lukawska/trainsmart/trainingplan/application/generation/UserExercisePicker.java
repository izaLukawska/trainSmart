package org.lukawska.trainsmart.trainingplan.application.generation;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.trainingplan.domain.entity.UserExercise;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@UtilityClass
public class UserExercisePicker {

    public static UserExercise pickExercise(List<UserExercise> exercises, Set<UserExercise> usedExercises) {
        List<UserExercise> notUsedExercises = exercises.stream()
                                                       .filter(userExercise -> !usedExercises.contains(userExercise))
                                                       .toList();
        if (notUsedExercises.isEmpty()) {
            log.debug("All exercises performed at least once in this plan. Choosing random exercise.");
            return getRandomUserExercise(exercises);
        }

        return notUsedExercises.stream()
                               .filter(userExercise -> userExercise.getLastUsedAt() == null)
                               .findAny()
                               .map(userExercise -> {
                                   log.debug("Picked never performed exercise: {}", userExercise.getId());
                                   return userExercise;
                               })
                               .orElseGet(() -> {
                                   log.debug("All exercises used at least once. Picking exercise not used in plan");
                                   return getRandomUserExercise(notUsedExercises);
                               });
    }

    private UserExercise getRandomUserExercise(List<UserExercise> userExercises) {
        UserExercise pickedExercise = userExercises.get(ThreadLocalRandom.current().nextInt(userExercises.size()));
        log.debug("Picked exercise: {}", pickedExercise.getId());
        return pickedExercise;
    }
}
