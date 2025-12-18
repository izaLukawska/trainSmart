package org.lukawska.trainsmart.usermanagement.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.usermanagement.application.dto.auth.request.LoginRequest;
import org.lukawska.trainsmart.usermanagement.application.dto.auth.request.RefreshTokenRequest;
import org.lukawska.trainsmart.usermanagement.application.dto.auth.response.AuthResponse;
import org.lukawska.trainsmart.usermanagement.application.service.auth.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest loginRequest) {
        return authenticationService.login(loginRequest);
    }

    @PostMapping("/refresh-token")
    public AuthResponse refreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        return authenticationService.refreshToken(refreshTokenRequest);
    }
}
