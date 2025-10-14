package org.lukawska.trainSmart.mailing.application.dto;

import java.time.Instant;
import java.util.List;

public record MailResponse(
		Long id,
		List<String> recipients,
		List<String> cc,
		List<String> bcc,
		String subject,
		String body,
		boolean isHtml,
		String from,
		String replyTo,
		Instant sentAt) {}
