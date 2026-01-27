package org.lukawska.trainsmart.mailing.testutil;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.mailing.application.dto.MailDetails;
import org.lukawska.trainsmart.mailing.domain.valueObjects.Attachment;
import org.lukawska.trainsmart.testutils.TestData;

import java.util.List;

@UtilityClass
public class MailingTestData {

    public static MailDetails mailDetailsWithAttachments() {
        return MailDetails.builder()
                          .recipients(List.of(TestData.email()))
                          .text(TestData.text())
                          .subject(TestData.text())
                          .attachments(validAttachments())
                          .isHtml(false)
                          .build();
    }

    private List<Attachment> validAttachments() {
        return List.of(new Attachment("file1.pdf", new byte[]{1, 2, 3}),
                       new Attachment("file2.pdf", new byte[]{1, 2, 3}));
    }
}
