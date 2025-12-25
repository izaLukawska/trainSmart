package org.lukawska.trainsmart.security.jwt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.security.jwt")
@Component
@Getter
@Setter
@Validated
public class JwtProperties {

    @NotBlank
    private String secret;

    @Positive
    private long accessExpirationMs;

}
