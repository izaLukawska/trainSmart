package org.lukawska.trainsmart.usermanagement.application.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ChangeEmailRequest(@NotBlank @Email String oldEmail, @NotBlank @Email String newMail) {}
