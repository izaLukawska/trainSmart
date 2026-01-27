package org.lukawska.trainsmart.testutils.builders;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.mailing.domain.entities.MailEntity;
import org.lukawska.trainsmart.mailing.domain.repositories.MailRepository;
import org.lukawska.trainsmart.mailing.domain.valueObjects.Attachment;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class MailingFixtureBuilder {

    private final MailRepository mailRepository;

    private List<String> recipients = new ArrayList<>(List.of("baseEmail@test.com"));

    private String subject = "base subject";

    private String text = "content";

    private boolean isHtml = false;

    private List<Attachment> attachments = new ArrayList<>(List.of(
            new Attachment("file1.pdf", new byte[]{1, 2, 3}),
            new Attachment("file2.pdf", new byte[]{1, 2, 3})));

    public MailingFixtureBuilder withRecipients(List<String> recipients) {
        this.recipients = recipients;
        return this;
    }

    public MailingFixtureBuilder withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public MailEntity build() {
        return MailEntity.builder()
                         .recipients(recipients)
                         .text(text)
                         .subject(subject)
                         .isHtml(isHtml)
                         .attachments(attachments)
                         .build();
    }

    public MailEntity save() {
        return mailRepository.save(build());
    }
}
