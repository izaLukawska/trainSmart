package org.lukawska.trainsmart.trainingplan.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanDetails;
import org.lukawska.trainsmart.trainingplan.application.preparation.dto.TrainingPlanGenerationData;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingPlan;
import org.lukawska.trainsmart.trainingplan.model.*;
import org.springframework.data.domain.Slice;

import java.util.List;

import static org.lukawska.trainsmart.trainingplan.application.mapper.TrainingWeekMapper.mapToTrainingWeekDetailsList;
import static org.lukawska.trainsmart.trainingplan.application.mapper.TrainingWeekMapper.mapToTrainingWeekResponse;

@UtilityClass
public class TrainingPlanMapper {

    public static TrainingPlanDetails mapToTrainingPlanDetails(TrainingPlan trainingPlan) {
        return new TrainingPlanDetails(trainingPlan.getTrainingType(),
                                       trainingPlan.getPlanDuration(),
                                       mapToTrainingWeekDetailsList(trainingPlan.getWeeks()));
    }

    public static TrainingPlanSummaryResponse mapToTrainingPlanSummary(TrainingPlan plan) {
        return TrainingPlanSummaryResponse.builder()
                                          .id(plan.getId())
                                          .trainingType(TrainingTypeEnum.valueOf(plan.getTrainingType().name()))
                                          .planDuration(PlanDurationEnum.valueOf(plan.getPlanDuration().name()))
                                          .daysPerWeek(plan.getDaysPerWeek())
                                          .createdAt(plan.getCreatedAt()).build();
    }

    public static SliceTrainingPlanSummaryResponse mapToTrainingPlanSummarySlice(Slice<TrainingPlan> slice) {
        return SliceTrainingPlanSummaryResponse.builder()
                                               .content(mapToTrainingPlanSummaryResponseList(slice.getContent()))
                                               .pageNumber(slice.getNumber())
                                               .pageSize(slice.getSize())
                                               .hasNext(slice.hasNext())
                                               .isFirst(slice.isFirst())
                                               .last(slice.isLast())
                                               .build();
    }

    public static TrainingPlan mapToTrainingPlan(TrainingPlanGenerationData data) {
        return new TrainingPlan(data.user(), data.trainingType(), data.planDuration(), data.preferredDays().size());
    }

    public static TrainingPlanResponse mapToTrainingPlanResponse(TrainingPlan trainingPlan) {
        return TrainingPlanResponse.builder()
                                   .planId(trainingPlan.getId())
                                   .planDuration(PlanDurationEnum.valueOf(trainingPlan.getPlanDuration().name()))
                                   .trainingType(TrainingTypeEnum.valueOf(trainingPlan.getTrainingType().name()))
                                   .trainingWeeks(mapToTrainingWeekResponse(trainingPlan.getWeeks()))
                                   .build();
    }

    private List<TrainingPlanSummaryResponse> mapToTrainingPlanSummaryResponseList(List<TrainingPlan> trainingPlans) {
        return trainingPlans.stream()
                            .map(TrainingPlanMapper::mapToTrainingPlanSummary)
                            .toList();

    }
}
