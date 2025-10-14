package org.lukawska.trainSmart.mailing.domain.valueObject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.io.InputStreamSource;

public record Attachment(@NotBlank String fileName,
                         @NotBlank AttachmentType attachmentType,
                         @NotNull InputStreamSource source) {}
