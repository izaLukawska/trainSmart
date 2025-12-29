package org.lukawska.trainsmart.usermanagement.application.dto.request.auth;

import jakarta.validation.constraints.NotNull;

public record RefreshTokenRequest(@NotNull String refreshToken) {}
