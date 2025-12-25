package org.lukawska.trainsmart.usermanagement.application.dto.user.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequest(@NotBlank String token, @NotBlank String newPassword) {}
