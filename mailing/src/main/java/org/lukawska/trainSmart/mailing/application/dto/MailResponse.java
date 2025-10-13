package org.lukawska.trainSmart.mailing.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.lukawska.trainSmart.mailing.domain.entities.Attachment;

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
		List<Attachment> attachmentList,
		LocalDateTime sentAt,
		boolean sentSuccess
) {}
