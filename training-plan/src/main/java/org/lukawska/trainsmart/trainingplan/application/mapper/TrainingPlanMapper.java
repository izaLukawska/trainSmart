package org.lukawska.trainsmart.trainingplan.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanRequest;
import org.lukawska.trainsmart.trainingplan.domain.entity.TrainingPlan;

@UtilityClass
public class TrainingPlanMapper {

    public static TrainingPlan mapToTrainingPlan(User user, TrainingPlanRequest request) {
        return new TrainingPlan(user, request.trainingType(), request.planDuration(), request.preferredDays().size());
    }
}
