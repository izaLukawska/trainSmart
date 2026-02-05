package org.lukawska.trainsmart.purchase.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.purchase.application.exception.PurchaseException;
import org.lukawska.trainsmart.purchase.application.exception.PurchaseExceptionType;
import org.lukawska.trainsmart.purchase.domain.entity.TrainingPlanPurchase;
import org.lukawska.trainsmart.purchase.domain.repository.TrainingPlanPurchaseRepository;
import org.lukawska.trainsmart.purchase.domain.service.PricingCalculator;
import org.lukawska.trainsmart.purchase.domain.valueObject.PurchaseStatus;
import org.lukawska.trainsmart.purchase.infrastructure.event.PurchaseCompletedEvent;
import org.lukawska.trainsmart.purchase.model.PurchaseRequest;
import org.lukawska.trainsmart.purchase.model.PurchaseResponse;
import org.lukawska.trainsmart.sharedpersistence.application.service.UserAccessService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.trainingplan.application.exception.TrainingPlanException;
import org.lukawska.trainsmart.trainingplan.application.service.TrainingPlanService;
import org.lukawska.trainsmart.trainingplan.domain.entities.TrainingPlan;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.lukawska.trainsmart.purchase.application.mapper.TrainingPlanPurchaseMapper.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingPlanPurchaseService {

    private final TrainingPlanPurchaseRepository purchaseRepository;

    private final TrainingPlanService trainingPlanService;

    private final UserAccessService userAccessService;

    private final PricingCalculator pricingCalculator;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PurchaseResponse purchaseCheckout(PurchaseRequest purchaseRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userAccessService.getUserByUsername(username);
        String paymentCode = UUID.randomUUID().toString();
        TrainingPlanPurchase trainingPlanPurchase = mapToPurchase(user, paymentCode, purchaseRequest);

        try {
            log.debug("Calculating per workout and total price for purchase");
            assignPurchasePrices(trainingPlanPurchase, purchaseRequest.getDaysPerWeek());
            TrainingPlanPurchase savedPurchase = purchaseRepository.save(trainingPlanPurchase);

            log.info("Successful create purchase {}", savedPurchase.getId());
            return mapToResponse(savedPurchase);
        } catch (DataIntegrityViolationException e) {
            throw new PurchaseException(PurchaseExceptionType.PURCHASE_ERROR);
        }
    }

    @Transactional(noRollbackFor = TrainingPlanException.class)
    public PurchaseResponse completePurchase(String paymentCode) {
        log.info("Validating training plan purchase for payment code {}", paymentCode);
        TrainingPlanPurchase trainingPlanPurchase = validatePurchaseData(paymentCode);

        try {
            User user = trainingPlanPurchase.getUser();
            Long userId = user.getId();
            TrainingPlan trainingPlan = trainingPlanService.createTrainingPlan(
                    userId, mapToTrainingPlanDto(trainingPlanPurchase));

            trainingPlanPurchase.attachTrainingPlan(trainingPlan);
            trainingPlanPurchase.markAsCompleted();
            eventPublisher.publishEvent(new PurchaseCompletedEvent(trainingPlan.getId(), userId, user.getEmail()));

            log.info("Purchase completed.");
            return mapToResponse(trainingPlanPurchase);
        } catch (TrainingPlanException e) {
            trainingPlanPurchase.markAsFailed();
            purchaseRepository.save(trainingPlanPurchase);
            throw new PurchaseException(PurchaseExceptionType.PURCHASE_ERROR);
        }
    }

    private TrainingPlanPurchase validatePurchaseData(String paymentCode) {
        TrainingPlanPurchase trainingPlanPurchase = purchaseRepository.findByPaymentCode(paymentCode).orElseThrow(
                () -> new PurchaseException(PurchaseExceptionType.PAYMENT_REQUIRED));

        if (trainingPlanPurchase.getPurchaseStatus() != PurchaseStatus.PENDING) {
            throw new PurchaseException(PurchaseExceptionType.PURCHASE_INVALID);
        }

        return trainingPlanPurchase;
    }

    private void assignPurchasePrices(TrainingPlanPurchase trainingPlanPurchase, int daysPerWeek) {
        PlanDuration planDuration = trainingPlanPurchase.getPlanDuration();
        BigDecimal finalPricePerDay = pricingCalculator.calculateFinalPricePerDay(planDuration);
        BigDecimal totalPrice = pricingCalculator.calculateTotalPrice(planDuration, daysPerWeek);

        trainingPlanPurchase.assignPricePerDay(finalPricePerDay);
        trainingPlanPurchase.assignTotalPrice(totalPrice);
    }
}
