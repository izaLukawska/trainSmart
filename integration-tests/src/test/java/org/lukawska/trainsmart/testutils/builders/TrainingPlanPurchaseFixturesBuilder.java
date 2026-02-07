package org.lukawska.trainsmart.testutils.builders;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.purchase.domain.entity.TrainingPlanPurchase;
import org.lukawska.trainsmart.purchase.domain.repository.TrainingPlanPurchaseRepository;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.WeekDay;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class TrainingPlanPurchaseFixturesBuilder {

    private final TrainingPlanPurchaseRepository purchaseRepository;

    private final TestFixtures testFixtures;

    private User user;

    private String paymentCode = UUID.randomUUID().toString();

    private TrainingType trainingType = TrainingType.STRENGTH;

    private PlanDuration planDuration = PlanDuration.FOUR_WEEKS;

    private int daysPerWeek = 3;

    private List<WeekDay> prefferedDays = new ArrayList<>();

    private BigDecimal pricePerDay = BigDecimal.TWO;

    private BigDecimal totalPrice = BigDecimal.TEN;

    public TrainingPlanPurchaseFixturesBuilder forUser(User user) {
        this.user = user;
        return this;
    }

    public TrainingPlanPurchase build() {
        User user = Optional.ofNullable(this.user).orElseGet(() -> testFixtures.user().save());
        TrainingPlanPurchase purchase = TrainingPlanPurchase.builder()
                                                            .user(user)
                                                            .paymentCode(paymentCode)
                                                            .planDuration(planDuration)
                                                            .trainingType(trainingType)
                                                            .daysPerWeek(daysPerWeek)
                                                            .preferredDays(prefferedDays)
                                                            .build();
        purchase.assignPricePerDay(this.pricePerDay);
        purchase.assignTotalPrice(totalPrice);
        return purchase;
    }

    public TrainingPlanPurchase save() {
        return purchaseRepository.save(build());
    }
}
