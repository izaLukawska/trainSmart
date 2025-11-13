package org.lukawska.trainsmart.openai.infra.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatRolesRequest(String systemPrompt, @NotBlank String userPrompt) {}
