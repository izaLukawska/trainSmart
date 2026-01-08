package org.lukawska.trainsmart.usermanagement.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.usermanagement.api.AuthApi;
import org.lukawska.trainsmart.usermanagement.application.service.AuthenticationService;
import org.lukawska.trainsmart.usermanagement.model.AuthResponse;
import org.lukawska.trainsmart.usermanagement.model.LoginRequest;
import org.lukawska.trainsmart.usermanagement.model.LogoutRequest;
import org.lukawska.trainsmart.usermanagement.model.RefreshTokenRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
public class AuthenticationController implements AuthApi {

    private final AuthenticationService authenticationService;

    @Override
    public ResponseEntity<AuthResponse> login(LoginRequest loginRequest) {
        log.info("Received login request for username {}", loginRequest.getUsername());
        return ResponseEntity.ok().body(authenticationService.login(loginRequest));
    }

    @Override
    public ResponseEntity<Void> logout(LogoutRequest logoutRequest) {
        log.info("Received logout request");
        authenticationService.logout(logoutRequest);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<AuthResponse> refreshToken(RefreshTokenRequest refreshTokenRequest) {
        log.info("Received refresh token request");
        return ResponseEntity.ok().body(authenticationService.refreshToken(refreshTokenRequest));
    }
}
