package org.lukawska.trainsmart.trainingplan.application.generation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class TrainingPlanStrategyFactory {

    public TrainingExerciseSelectionStrategy pick(boolean firstPlan, int newExercisesCount, Set<String> injuries,
                                                  boolean injuriesChanged) {
        return null;
    }
}
