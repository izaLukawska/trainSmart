package org.lukawska.trainsmart.trainingplan.application.service.strategy;

import java.util.List;
import java.util.Set;

public interface EnableExerciseStrategy {

    boolean matches(EnableExerciseStrategyContext context);

    void updateStatus(Long userId, Set<String> injuries, List<String> newExerciseNames);
}
