package org.lukawska.trainsmart.purchase.domain.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.purchase.infrastructure.config.PricingConfig;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricingCalculatorTest {

    private static final PlanDuration PLAN_DURATION = PlanDuration.EIGHT_WEEKS;

    private static final Map<PlanDuration, BigDecimal> MULTIPLIER = Map.of(PLAN_DURATION, BigDecimal.ONE);

    @Mock
    private PricingConfig pricingConfig;

    @InjectMocks
    private PricingCalculator pricingCalculator;

    @Test
    void shouldReturnTotalPriceWhenCalculateTotalPrice() {
        //given
        final int daysPerWeek = daysPerWeek();
        when(pricingConfig.getBaseRate()).thenReturn(BigDecimal.ONE);
        when(pricingConfig.getDurationMultipliers()).thenReturn(MULTIPLIER);
        BigDecimal expectedPrice = BigDecimal.valueOf(PLAN_DURATION.getWeeksCount())
                                             .multiply(BigDecimal.valueOf(daysPerWeek))
                                             .setScale(2, RoundingMode.HALF_UP);

        //when
        BigDecimal result = pricingCalculator.calculateTotalPrice(PLAN_DURATION, daysPerWeek);

        //then
        assertThat(result).isEqualTo(expectedPrice);
    }

    @Test
    void shouldReturnFinalPricePerDayWhenCalculateFinalPricePerDay() {
        //given
        when(pricingConfig.getBaseRate()).thenReturn(BigDecimal.ONE);
        when(pricingConfig.getDurationMultipliers()).thenReturn(MULTIPLIER);
        BigDecimal expectedPrice = BigDecimal.ONE;

        //when
        BigDecimal result = pricingCalculator.calculateFinalPricePerDay(PLAN_DURATION);

        //then
        assertThat(result).isEqualTo(expectedPrice);

    }

    private int daysPerWeek() {
        return new Random().nextInt(7) + 1;
    }
}
