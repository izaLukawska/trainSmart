package org.lukawska.trainsmart.security.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        String schemaKey = "bearerAuth";
        SecurityScheme securityScheme = buildSecurityScheme();

        return new OpenAPI().components(new Components().addSecuritySchemes(schemaKey, securityScheme))
                            .addSecurityItem(new SecurityRequirement().addList(schemaKey));
    }

    private SecurityScheme buildSecurityScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                                   .scheme("bearer")
                                   .bearerFormat("JWT")
                                   .name("Authorization")
                                   .in(SecurityScheme.In.HEADER);
    }
}
