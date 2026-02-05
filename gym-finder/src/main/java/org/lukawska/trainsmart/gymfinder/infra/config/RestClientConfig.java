package org.lukawska.trainsmart.gymfinder.infra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Value("${app.gym-provider.base-url}")
    private String gymApiBaseUrl;

    @Bean
    public RestClient customRestClient(RestClient.Builder builder) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setReadTimeout(Duration.ofSeconds(10));

        return builder.baseUrl(gymApiBaseUrl)
                      .requestFactory(factory)
                      .build();
    }
}
