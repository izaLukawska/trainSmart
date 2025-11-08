package org.lukawska.trainSmart.mailing.application.dto;

import jakarta.validation.constraints.*;
import org.lukawska.trainSmart.mailing.domain.valueObject.Attachment;

import java.util.List;

public record MailRequest(
        @NotEmpty List<@Email String> recipients,
        @NotNull List<@Email String> cc,
        @NotNull List<@Email String> bcc,
        @NotBlank String subject,
        @NotBlank String text,
        boolean isHtml,
        @NotNull @Size(max = 5) List<Attachment> attachments) {}
