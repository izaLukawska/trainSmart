package org.lukawska.trainsmart.usermanagement.application.dto.auth.request;

import jakarta.validation.constraints.NotNull;

public record RefreshTokenRequest(@NotNull String refreshToken) {}
