package org.lukawska.trainsmart.statements.infra.config;

import jakarta.validation.constraints.NotBlank;

public record Statement(@NotBlank String title, int version, boolean required, @NotBlank String content) {}
