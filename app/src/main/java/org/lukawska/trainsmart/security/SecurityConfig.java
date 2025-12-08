package org.lukawska.trainsmart.security;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(authorize -> authorize
                           .requestMatchers("/exercises/**").hasRole("ADMIN")
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
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                               .username("user")
                               .password(passwordEncoder().encode("user"))
                               .roles("USER")
                               .build();

        UserDetails admin = User.builder()
                                .username("admin")
                                .password(passwordEncoder().encode("admin"))
                                .roles("USER", "ADMIN")
                                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }
}
