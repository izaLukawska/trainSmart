package org.lukawska.trainsmart.trainingplan.domain.specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingPlan;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TrainingPlanSpecifications {

    public static Specification<TrainingPlan> byUserId(Long userId) {
        return (root, query, cb) -> userId == null ? null : cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<TrainingPlan> trainingTypeEquals(TrainingType trainingType) {
        return (root, query, cb) -> trainingType == null ? null : cb.equal(root.get("trainingType"), trainingType);
    }

    public static Specification<TrainingPlan> planDurationEquals(PlanDuration planDuration) {
        return (root, query, cb) -> planDuration == null ? null : cb.equal(root.get("planDuration"), planDuration);
    }
}
