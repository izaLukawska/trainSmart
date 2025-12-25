package org.lukawska.trainsmart.usermanagement.application.dto.user.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordUpdateRequest(@NotBlank String currentPassword, @NotBlank String newPassword) {}
