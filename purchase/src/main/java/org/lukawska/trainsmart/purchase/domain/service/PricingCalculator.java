package org.lukawska.trainsmart.purchase.domain.service;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.purchase.infrastructure.config.PricingConfig;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RequiredArgsConstructor
@Service
public class PricingCalculator {

    private final PricingConfig pricingConfig;

    public BigDecimal calculateTotalPrice(PlanDuration planDuration, int daysPerWeek) {
        int totalWorkouts = daysPerWeek * planDuration.getWeeksCount();

        return calculateFinalPricePerDay(planDuration).multiply(BigDecimal.valueOf(totalWorkouts))
                                                      .setScale(2, RoundingMode.HALF_UP);

    }

    public BigDecimal calculateFinalPricePerDay(PlanDuration planDuration) {
        BigDecimal discountMultiplier = pricingConfig.getDurationMultipliers()
                                                     .getOrDefault(planDuration, BigDecimal.ONE);
        return pricingConfig.getBaseRate().multiply(discountMultiplier);
    }
}
