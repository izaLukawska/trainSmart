package org.lukawska.trainsmart.usermanagement.infra.config;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app.security.refresh-token")
public class RefreshTokenProperties {

    @Positive
    private long expirationMs;

}
