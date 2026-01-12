package org.lukawska.trainsmart.security.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    private static final String SCHEMA_KEY = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme securityScheme = buildSecurityScheme();
        Components components = buildComponents(securityScheme);
        SecurityRequirement securityRequirement = buildSecurityRequirement();

        return new OpenAPI().components(components)
                            .addSecurityItem(securityRequirement);
    }

    private Components buildComponents(SecurityScheme securityScheme) {
        return new Components().addSecuritySchemes(SCHEMA_KEY, securityScheme);
    }

    private SecurityRequirement buildSecurityRequirement() {
        return new SecurityRequirement().addList(SCHEMA_KEY);
    }

    private SecurityScheme buildSecurityScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                                   .scheme("bearer")
                                   .bearerFormat("JWT");
    }
}
