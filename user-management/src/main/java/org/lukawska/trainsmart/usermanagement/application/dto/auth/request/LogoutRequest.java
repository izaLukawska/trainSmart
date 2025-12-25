package org.lukawska.trainsmart.usermanagement.application.dto.auth.request;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(@NotBlank String refreshToken) {}
