package org.lukawska.trainSmart.mailing.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.io.InputStreamSource;

public record Attachment(@NotBlank String fileName, @NotBlank String mimeType, @NotNull InputStreamSource source) {}
