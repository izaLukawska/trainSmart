package org.lukawska.trainSmart.mailing.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.lukawska.trainSmart.mailing.domain.valueObject.Attachment;

import java.util.List;

public record MailRequest(
        @NotEmpty(message = "At least one recipient is required.") List<String> recipients,
        @NotNull List<String> cc,
        @NotNull List<String> bcc,
        @NotBlank(message = "Subject cannot be blank.") String subject,
        @NotBlank(message = "Body cannot be blank.") String body,
        boolean isHtml,
        @NotNull List<Attachment> attachments) {}
