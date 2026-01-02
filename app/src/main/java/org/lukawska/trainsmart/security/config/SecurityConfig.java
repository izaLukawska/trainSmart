package org.lukawska.trainsmart.security.config;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.security.jwt.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    private static final String[] WHITELIST = {
            "/users/register",
            "/users/activate",
            "/users/password-reset",
            "/users/send-verification-link",
            "/auth/**",
            "/actuator/**"
    };

    private static final String[] ADMIN_ONLY = {"/exercises", "/mail/**"};

    private static final String[] AUTHENTICATED_ONLY = {
            "/auth/logout",
            "/users/**/agreements",
            "/users/**/health-survey"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(authorize -> authorize
                           .requestMatchers(WHITELIST).permitAll()
                           .requestMatchers(AUTHENTICATED_ONLY).authenticated()
                           .requestMatchers(ADMIN_ONLY).hasRole("ADMIN")
                           .requestMatchers("/users/**").hasRole("USER")
                           .anyRequest().authenticated())
                   .csrf(AbstractHttpConfigurer::disable)
                   .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                   .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                   .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
