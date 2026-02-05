package org.lukawska.trainsmart.purchase.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.purchase.domain.entity.TrainingPlanPurchase;
import org.lukawska.trainsmart.purchase.model.OrderStatusEnum;
import org.lukawska.trainsmart.purchase.model.PurchaseRequest;
import org.lukawska.trainsmart.purchase.model.PurchaseResponse;
import org.lukawska.trainsmart.purchase.model.WeekDaysEnum;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.application.dto.TrainingPlanDto;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.WeekDay;
import org.springframework.util.CollectionUtils;

import java.util.List;

@UtilityClass
public class TrainingPlanPurchaseMapper {

    public static TrainingPlanDto mapToTrainingPlanDto(TrainingPlanPurchase purchase) {
        return new TrainingPlanDto(purchase.getTrainingType(), purchase.getPlanDuration(),
                                   purchase.getDaysPerWeek(), purchase.getPreferredDays());
    }

    public static TrainingPlanPurchase mapToPurchase(User user, String paymentCode, PurchaseRequest purchaseRequest) {
        return TrainingPlanPurchase.builder()
                                   .user(user)
                                   .paymentCode(paymentCode)
                                   .daysPerWeek(purchaseRequest.getDaysPerWeek())
                                   .planDuration(PlanDuration.valueOf(purchaseRequest.getPlanDuration().name()))
                                   .trainingType(TrainingType.valueOf(purchaseRequest.getTrainingType().name()))
                                   .preferredDays(mapToWeekDays(purchaseRequest.getPreferredDays()))
                                   .build();
    }

    public static PurchaseResponse mapToResponse(TrainingPlanPurchase trainingPlanPurchase) {
        return PurchaseResponse.builder()
                               .purchaseId(trainingPlanPurchase.getId())
                               .orderStatus(OrderStatusEnum.valueOf(trainingPlanPurchase.getPurchaseStatus().name()))
                               .finalPrice(trainingPlanPurchase.getTotalPrice())
                               .build();
    }

    private List<WeekDay> mapToWeekDays(List<WeekDaysEnum> requestWeekDays) {
        if (CollectionUtils.isEmpty(requestWeekDays)) {
            return List.of();
        }

        return requestWeekDays.stream()
                              .map(requestWeekDay -> WeekDay.valueOf(requestWeekDay.name()))
                              .toList();
    }
}
