package org.lukawska.trainsmart.trainingplan.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanFilterDto;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;
import org.lukawska.trainsmart.trainingplan.model.PlanDurationEnum;
import org.lukawska.trainsmart.trainingplan.model.TrainingPlanFilterRequest;
import org.lukawska.trainsmart.trainingplan.model.TrainingTypeEnum;

@UtilityClass
public class TrainingPlanFilterMapper {

    public static TrainingPlanFilterDto mapToTrainingPlanFilterDto(TrainingPlanFilterRequest request) {
        TrainingType trainingType = resolveTrainingTypeValue(request.getTrainingType());
        PlanDuration planDuration = resolvePlanDurationValue(request.getPlanDuration());

        return TrainingPlanFilterDto.builder()
                                    .trainingType(trainingType)
                                    .planDuration(planDuration)
                                    .build();
    }

    private TrainingType resolveTrainingTypeValue(TrainingTypeEnum trainingTypeEnum) {
        return trainingTypeEnum == null ? null : TrainingType.valueOf(trainingTypeEnum.name());
    }

    private PlanDuration resolvePlanDurationValue(PlanDurationEnum planDurationEnum) {
        return planDurationEnum == null ? null : PlanDuration.valueOf(planDurationEnum.name());
    }
}
