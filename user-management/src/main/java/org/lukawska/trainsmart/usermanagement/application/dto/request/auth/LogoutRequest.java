package org.lukawska.trainsmart.usermanagement.application.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(@NotBlank String refreshToken) {}
