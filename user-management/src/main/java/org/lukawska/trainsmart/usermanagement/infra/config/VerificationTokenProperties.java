package org.lukawska.trainsmart.usermanagement.infra.config;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Component
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app.security.verification-token")
public class VerificationTokenProperties {

    @Positive
    @DurationUnit(ChronoUnit.HOURS)
    private Duration expirationHours;

}
