package org.lukawska.trainsmart.purchase.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lukawska.trainsmart.purchase.domain.valueObject.PurchaseStatus;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.sharedpersistence.infrastructure.audit.AuditableEntity;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingPlan;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.TrainingType;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.WeekDay;

import java.math.BigDecimal;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "training_plan_purchases")
public class TrainingPlanPurchase extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String paymentCode;

    @Enumerated(value = EnumType.STRING)
    private PurchaseStatus purchaseStatus = PurchaseStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    private TrainingPlan trainingPlan;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal totalPrice;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal pricePerDay;

    @Enumerated(value = EnumType.STRING)
    private TrainingType trainingType;

    @Enumerated(value = EnumType.STRING)
    private PlanDuration planDuration;

    private int daysPerWeek;

    @ElementCollection(targetClass = WeekDay.class)
    @CollectionTable(name = "purchase_preferred_days", joinColumns = @JoinColumn(name = "purchase_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private List<WeekDay> preferredDays;

    @Builder
    private TrainingPlanPurchase(String paymentCode, User user, TrainingType trainingType,
                                 PlanDuration planDuration, int daysPerWeek, List<WeekDay> preferredDays) {
        this.paymentCode = paymentCode;
        this.user = user;
        this.trainingType = trainingType;
        this.planDuration = planDuration;
        this.daysPerWeek = daysPerWeek;
        this.preferredDays = preferredDays;
    }

    public void assignTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void assignPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public void attachTrainingPlan(TrainingPlan trainingPlan) {
        this.trainingPlan = trainingPlan;
    }

    public void markAsFailed() {
        this.purchaseStatus = PurchaseStatus.FAILED;
    }

    public void markAsCompleted() {
        this.purchaseStatus = PurchaseStatus.COMPLETED;
    }
}
