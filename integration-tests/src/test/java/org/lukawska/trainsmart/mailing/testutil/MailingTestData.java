package org.lukawska.trainsmart.mailing.testutil;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.mailing.application.dto.MailDetails;
import org.lukawska.trainsmart.mailing.application.dto.MailResponse;
import org.lukawska.trainsmart.mailing.domain.entities.MailEntity;
import org.lukawska.trainsmart.mailing.domain.valueObjects.Attachment;

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
                         .recipients(List.of(randomEmail()))
                         .text(UUID.randomUUID().toString())
                         .subject(UUID.randomUUID().toString())
                         .isHtml(new Random().nextBoolean())
                         .attachments(validAttachments())
                         .build();
    }

    public static MailEntity mailWithSubject(String subject) {
        return MailEntity.builder()
                         .recipients(List.of(randomEmail()))
                         .text(UUID.randomUUID().toString())
                         .subject(subject)
                         .isHtml(new Random().nextBoolean())
                         .attachments(validAttachments())
                         .build();
    }

    public static MailDetails mailDetailsWithAttachments() {
        return MailDetails.builder()
                          .recipients(List.of(randomEmail()))
                          .text(randomString())
                          .subject(randomString())
                          .attachments(validAttachments())
                          .isHtml(false)
                          .build();
    }

    public static MailDetails mailDetailsWithInvalidAttachment() {
        return MailDetails.builder()
                          .recipients(List.of(randomEmail()))
                          .text(randomString())
                          .subject(randomString())
                          .isHtml(false)
                          .attachments(List.of(new Attachment("file.txt", new byte[]{1, 2, 3}),
                                               new Attachment("file.pdf", new byte[]{1, 2, 3})))
                          .build();
    }

    public static MailResponse mailResponseWithId(Long id) {
        return new MailResponse(id,
                                List.of(randomEmail()),
                                List.of(randomEmail()),
                                randomString(),
                                "test-from@example.com",
                                "test-replyto@example.com",
                                List.of(),
                                Instant.now());
    }

    public static MailResponse mailResponseWithRecipient(String recipient) {
        return new MailResponse(new Random().nextLong(100),
                                List.of(recipient),
                                List.of(randomEmail()),
                                randomString(),
                                "test-from@example.com",
                                "test-replyto@example.com",
                                List.of(),
                                Instant.now());
    }

    public MailResponse mailResponseWithSubject(String subject) {
        return new MailResponse(new Random().nextLong(100),
                                List.of(randomEmail()),
                                List.of(randomEmail()),
                                subject,
                                "test-from@example.com",
                                "test-replyto@example.com",
                                List.of(),
                                Instant.now());
    }

    public static String randomEmail() {
        return randomString() + "@example.com";
    }

    private static String randomString() {
        return UUID.randomUUID().toString();
    }

    private List<Attachment> validAttachments() {
        return List.of(new Attachment("file1.pdf", new byte[]{1, 2, 3}),
                       new Attachment("file2.pdf", new byte[]{1, 2, 3}));
    }
}
