package org.lukawska.trainsmart.trainingplan.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TrainingPlanFilterRequest {

    private TrainingType trainingType;

    private PlanDuration planDuration;

    public TrainingPlanFilterRequest(TrainingType trainingType) {
        this.trainingType = trainingType;
    }

    public TrainingPlanFilterRequest(PlanDuration planDuration) {
        this.planDuration = planDuration;
    }
}
