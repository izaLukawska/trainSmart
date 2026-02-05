package org.lukawska.trainsmart.purchase.application.service;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.purchase.domain.entity.TrainingPlanPurchase;
import org.lukawska.trainsmart.purchase.model.PlanDurationEnum;
import org.lukawska.trainsmart.purchase.model.PurchaseRequest;
import org.lukawska.trainsmart.purchase.model.TrainingTypeEnum;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingPlan;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.mockito.Mockito.mock;

@UtilityClass
public class PurchaseTestData {

    static PurchaseRequest purchaseRequest() {
        return PurchaseRequest.builder()
                              .planDuration(PlanDurationEnum.EIGHT_WEEKS)
                              .trainingType(TrainingTypeEnum.STRENGTH)
                              .daysPerWeek(3)
                              .build();
    }

    static TrainingPlanPurchase trainingPlanPurchase() {
        return TrainingPlanPurchase.builder()
                                   .user(mock(User.class))
                                   .daysPerWeek(new Random().nextInt(7) + 1)
                                   .preferredDays(List.of())
                                   .planDuration(PlanDuration.EIGHT_WEEKS)
                                   .trainingType(TrainingType.STRENGTH)
                                   .build();
    }

    static String username() {
        return "user" + new Random().nextInt();
    }

    static String paymentCode() {
        return UUID.randomUUID().toString();
    }

    static TrainingPlan trainingPlan() {
        return mock(TrainingPlan.class);
    }
}
