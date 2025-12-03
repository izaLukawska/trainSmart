package org.lukawska.trainsmart.trainingplan.application.service.strategy;

import java.util.List;

public interface EnableExerciseStrategy {

    boolean matches(EnableExerciseStrategyContext context);

    void updateStatus(Long userId, List<String> newExerciseNames);
}
