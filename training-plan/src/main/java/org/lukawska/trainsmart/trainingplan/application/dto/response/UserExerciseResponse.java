package org.lukawska.trainsmart.trainingplan.application.dto.response;

import java.time.Instant;

public record UserExerciseResponse(Long id, String exerciseName, boolean enabled, Instant lastUsedAt) {}
