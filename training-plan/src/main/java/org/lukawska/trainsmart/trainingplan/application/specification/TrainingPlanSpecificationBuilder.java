package org.lukawska.trainsmart.trainingplan.application.specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.lukawska.trainsmart.trainingplan.application.dto.request.TrainingPlanFilterRequest;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingPlan;
import org.lukawska.trainsmart.trainingplan.domain.specification.TrainingPlanSpecifications;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TrainingPlanSpecificationBuilder {

    public static Specification<TrainingPlan> build(Long userId, TrainingPlanFilterRequest filterRequest) {
        return Specification.allOf(TrainingPlanSpecifications.byUserId(userId))
                            .and(TrainingPlanSpecifications.trainingTypeEquals(filterRequest.getTrainingType()))
                            .and(TrainingPlanSpecifications.planDurationEquals(filterRequest.getPlanDuration()));
    }
}
