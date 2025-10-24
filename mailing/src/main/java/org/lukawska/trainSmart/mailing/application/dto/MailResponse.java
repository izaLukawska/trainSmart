package org.lukawska.trainSmart.mailing.application.dto;

import java.time.Instant;
import java.util.List;

public record MailResponse(
        Long id,
        List<String> recipients,
        List<String> cc,
        List<String> bcc,
        String subject,
        String from,
        String replyTo,
        List<AttachmentMeta> attachments,
        Instant sentAt) {}
