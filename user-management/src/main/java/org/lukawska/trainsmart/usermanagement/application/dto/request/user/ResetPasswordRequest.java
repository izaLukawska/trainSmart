package org.lukawska.trainsmart.usermanagement.application.dto.request.user;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(@NotBlank String token, @NotBlank String newPassword) {}
