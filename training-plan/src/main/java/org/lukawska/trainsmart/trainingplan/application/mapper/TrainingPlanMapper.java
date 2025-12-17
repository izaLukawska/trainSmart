package org.lukawska.trainsmart.trainingplan.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.trainingplan.application.dto.response.TrainingPlanResponse;
import org.lukawska.trainsmart.trainingplan.application.dto.response.TrainingPlanSummaryResponse;
import org.lukawska.trainsmart.trainingplan.application.preparation.dto.TrainingPlanGenerationData;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingPlan;
import org.springframework.data.domain.Slice;

import static org.lukawska.trainsmart.trainingplan.application.mapper.TrainingWeekMapper.mapToTrainingWeekResponseList;

@UtilityClass
public class TrainingPlanMapper {

    public static TrainingPlanResponse mapToTrainingPlanResponse(TrainingPlan trainingPlan) {
        return new TrainingPlanResponse(trainingPlan.getId(),
                                        trainingPlan.getTrainingType(),
                                        trainingPlan.getPlanDuration(),
                                        mapToTrainingWeekResponseList(trainingPlan.getWeeks()));
    }

    public static TrainingPlanSummaryResponse mapToTrainingPlanSummary(TrainingPlan trainingPlan) {
        return new TrainingPlanSummaryResponse(trainingPlan.getId(), trainingPlan.getTrainingType(),
                                               trainingPlan.getPlanDuration(), trainingPlan.getDaysPerWeek(),
                                               trainingPlan.getCreatedAt());
    }

    public static Slice<TrainingPlanSummaryResponse> mapToTrainingPlanSummarySlice(Slice<TrainingPlan> trainingPlans) {
        return trainingPlans.map(TrainingPlanMapper::mapToTrainingPlanSummary);
    }

    public static TrainingPlan mapToTrainingPlan(TrainingPlanGenerationData data) {
        return new TrainingPlan(data.user(), data.trainingType(), data.planDuration(), data.preferredDays().size());
    }
}
