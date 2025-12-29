package org.lukawska.trainsmart.usermanagement.presentation.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.usermanagement.application.dto.request.auth.LoginRequest;
import org.lukawska.trainsmart.usermanagement.application.dto.request.auth.LogoutRequest;
import org.lukawska.trainsmart.usermanagement.application.dto.request.auth.RefreshTokenRequest;
import org.lukawska.trainsmart.usermanagement.application.dto.response.AuthResponse;
import org.lukawska.trainsmart.usermanagement.application.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest loginRequest) {
        log.debug("Received login request for username {}", loginRequest.username());
        return authenticationService.login(loginRequest);
    }

    @PostMapping("/refresh-token")
    public AuthResponse refreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        log.debug("Received refresh token request");
        return authenticationService.refreshToken(refreshTokenRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid LogoutRequest logoutRequest, HttpServletResponse response) {
        log.debug("Received logout request");
        authenticationService.logout(logoutRequest, response);
        return ResponseEntity.noContent().build();
    }
}
