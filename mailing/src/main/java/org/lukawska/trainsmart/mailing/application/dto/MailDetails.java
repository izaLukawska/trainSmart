package org.lukawska.trainsmart.mailing.application.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import org.lukawska.trainsmart.mailing.domain.valueObjects.Attachment;

import java.util.List;

@Builder
public record MailDetails(@NotEmpty List<@Email String> recipients,
                          List<@Email String> cc,
                          List<@Email String> bcc,
                          @NotBlank String subject,
                          @NotBlank String text,
                          boolean isHtml,
                          @Size(max = 5) List<@NotNull Attachment> attachments) {}
