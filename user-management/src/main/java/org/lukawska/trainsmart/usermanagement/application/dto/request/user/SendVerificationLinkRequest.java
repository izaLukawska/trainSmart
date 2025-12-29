package org.lukawska.trainsmart.usermanagement.application.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.lukawska.trainsmart.usermanagement.domain.valueObject.TokenType;

public record SendVerificationLinkRequest(@NotBlank String username, @NotNull TokenType tokenType) {}
