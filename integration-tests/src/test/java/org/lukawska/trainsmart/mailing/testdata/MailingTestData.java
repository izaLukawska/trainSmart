package org.lukawska.trainsmart.mailing.testdata;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.mailing.application.dto.MailRequest;
import org.lukawska.trainsmart.mailing.application.dto.MailResponse;
import org.lukawska.trainsmart.mailing.domain.entities.MailEntity;
import org.lukawska.trainsmart.mailing.domain.valueObject.Attachment;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@UtilityClass
public class MailingTestData {

    public static MailEntity mailWithRecipient(String recipient) {
        return MailEntity.builder()
                         .recipients(List.of(recipient))
                         .text(UUID.randomUUID().toString())
                         .subject(UUID.randomUUID().toString())
                         .isHtml(new Random().nextBoolean())
                         .attachments(validAttachments())
                         .build();
    }

    public static MailEntity mailEntityWithAttachments() {
        return MailEntity.builder()
                         .recipients(List.of(UUID.randomUUID() + "@test.com"))
                         .text(UUID.randomUUID().toString())
                         .subject(UUID.randomUUID().toString())
                         .isHtml(new Random().nextBoolean())
                         .attachments(validAttachments())
                         .build();
    }

    public static MailEntity mailWithSubject(String subject) {
        return MailEntity.builder()
                         .recipients(List.of(UUID.randomUUID() + "@test.com"))
                         .text(UUID.randomUUID().toString())
                         .subject(subject)
                         .isHtml(new Random().nextBoolean())
                         .attachments(validAttachments())
                         .build();
    }

    public static MailRequest mailRequestWithAttachments() {
        return new MailRequest(List.of(UUID.randomUUID() + "@test.com"),
                               List.of(),
                               List.of(),
                               UUID.randomUUID().toString(),
                               UUID.randomUUID().toString(),
                               new Random().nextBoolean(),
                               validAttachments());
    }

    public static MailRequest mailRequestWithInvalidAttachment() {
        return new MailRequest(List.of(UUID.randomUUID() + "@test.com"),
                               List.of(),
                               List.of(),
                               UUID.randomUUID().toString(),
                               UUID.randomUUID().toString(),
                               new Random().nextBoolean(),
                               List.of(new Attachment("file.txt", new byte[]{1, 2, 3}),
                                       new Attachment("file.pdf", new byte[]{1, 2, 3})));
    }

    public static MailResponse mailResponseWitId(Long id) {
        return new MailResponse(id,
                                List.of(UUID.randomUUID() + "@test.com"),
                                List.of(UUID.randomUUID() + "@test.com"),
                                UUID.randomUUID().toString(),
                                "test-from@example.com",
                                "test-replyto@example.com",
                                List.of(),
                                Instant.now());
    }

    public static MailResponse mailResponseWithRecipient(String recipient) {
        return new MailResponse(new Random().nextLong(100),
                                List.of(recipient),
                                List.of(UUID.randomUUID() + "@test.com"),
                                UUID.randomUUID().toString(),
                                "test-from@example.com",
                                "test-replyto@example.com",
                                List.of(),
                                Instant.now());
    }

    public static MailResponse mailResponseWithSubject(String subject) {
        return new MailResponse(new Random().nextLong(100),
                                List.of(UUID.randomUUID() + "@test.com"),
                                List.of(UUID.randomUUID() + "@test.com"),
                                subject,
                                "test-from@example.com",
                                "test-replyto@example.com",
                                List.of(),
                                Instant.now());
    }

    private static List<Attachment> validAttachments() {
        return List.of(new Attachment("file1.pdf", new byte[]{1, 2, 3}),
                       new Attachment("file2.pdf", new byte[]{1, 2, 3}));
    }
}
