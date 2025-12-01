package org.lukawska.trainsmart.trainingplan.application.generation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.WeekDay;

import java.util.List;

public record TrainingPlanContext(User user,
                                  TrainingType trainingType,
                                  PlanDuration planDuration,
                                  @Size(min = 1, max = 7) List<@NotNull WeekDay> preferredDays) {
}
