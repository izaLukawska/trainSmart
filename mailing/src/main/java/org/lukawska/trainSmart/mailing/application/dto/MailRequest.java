package org.lukawska.trainSmart.mailing.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.lukawska.trainSmart.mailing.domain.entities.Attachment;

import java.util.List;

public record MailRequest(
		@NotEmpty(message = "At least one recipient is required.") List<String> recipients,
		List<String> cc,
		List<String> bcc,
		@NotBlank(message = "Subject cannot be blank.") String subject,
		@NotBlank(message = "Body cannot be blank.") String body,
		boolean isHtml,
		List<Attachment> attachmentList) {}
