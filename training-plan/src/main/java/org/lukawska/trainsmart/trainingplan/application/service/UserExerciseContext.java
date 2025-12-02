package org.lukawska.trainsmart.trainingplan.application.service;

import java.util.List;
import java.util.Set;

public record UserExerciseContext(Set<String> injuries, boolean injuriesChanged, List<String> addedExercises) {}
