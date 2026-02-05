package org.lukawska.trainsmart.testutils;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.mailing.application.dto.MailDetails;
import org.lukawska.trainsmart.mailing.domain.valueObjects.Attachment;

import java.util.List;
import java.util.Random;

@UtilityClass
public class TestData {

    public static String rawPassword() {
        return "password" + randomInteger();
    }

    public static String email() {
        return randomInteger() + "@email.com";
    }

    public static String username() {
        return "user" + randomInteger();
    }

    public static String tokenValue() {
        return randomInteger() + "tokenValue";
    }

    public static String exerciseName() {
        return "exercise" + randomInteger();
    }

    public static String text() {
        return "text content" + randomInteger();
    }

    public static MailDetails mailDetailsWithAttachments() {
        return MailDetails.builder()
                          .recipients(List.of(TestData.email()))
                          .text(TestData.text())
                          .subject(TestData.text())
                          .attachments(List.of(new Attachment("file1.pdf", new byte[]{1, 2, 3})))
                          .isHtml(false)
                          .build();
    }

    private Integer randomInteger() {
        return new Random().nextInt();
    }
}
