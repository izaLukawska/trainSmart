package org.lukawska.trainsmart.purchase.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.lukawska.trainsmart.trainingplan.domain.valueObjects.PlanDuration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.pricing")
@Component
public class PricingConfig {

    private BigDecimal baseRate;

    private Map<PlanDuration, BigDecimal> durationMultipliers = new HashMap<>();

}
