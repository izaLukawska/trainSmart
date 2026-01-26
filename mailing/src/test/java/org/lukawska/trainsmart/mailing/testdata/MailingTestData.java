package org.lukawska.trainsmart.mailing.testdata;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.mailing.application.dto.MailDetails;
import org.lukawska.trainsmart.mailing.domain.entities.MailEntity;
import org.lukawska.trainsmart.mailing.domain.valueObjects.Attachment;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@UtilityClass
public final class MailingTestData {

    public static MailDetails mailDetails(boolean hasAttachments) {
        List<Attachment> attachments = hasAttachments ? List.of(randomValidAttachment()) : List.of();
        return new MailDetails(List.of(randomMail()),
                               List.of(randomMail()),
                               List.of(randomMail()),
                               randomText(),
                               randomText(),
                               new Random().nextBoolean(),
                               attachments);
    }

    public static MailEntity randomMailEntity() {
        return MailEntity.builder()
                         .recipients(List.of(randomMail()))
                         .cc(List.of(randomMail()))
                         .bcc(List.of(randomMail()))
                         .text(randomText())
                         .subject(randomText())
                         .isHtml(new Random().nextBoolean())
                         .attachments(List.of(randomValidAttachment()))
                         .build();
    }

    public static Attachment randomValidAttachment() {
        return new Attachment(randomText().concat(".pdf"), new byte[10]);
    }

    public static String randomMail() {
        return UUID.randomUUID() + "@test.com";
    }

    public static String randomText() {
        return UUID.randomUUID().toString();
    }
}
