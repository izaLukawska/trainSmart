package org.lukawska.trainSmart.mailing.domain.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lukawska.trainSmart.mailing.domain.valueObject.Attachment;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "mails")
public class MailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "mail_recipients", joinColumns = @JoinColumn(name = "mail_id"))
    @Column(name = "recipient")
    private List<String> recipients = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "mail_cc", joinColumns = @JoinColumn(name = "mail_id"))
    @Column(name = "cc")
    private List<String> cc = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "mail_bcc", joinColumns = @JoinColumn(name = "mail_id"))
    @Column(name = "bcc")
    private List<String> bcc = new ArrayList<>();

    @Column(nullable = false)
    private String subject;

    @Lob
    @Column(nullable = false)
    private String text;

    private boolean isHtml;

    @ElementCollection
    @CollectionTable(name = "mail_attachments", joinColumns = @JoinColumn(name = "mail_id"))
    private List<Attachment> attachments = new ArrayList<>();

    private Instant sentAt;

    @Builder
    protected MailEntity(List<String> recipients,
                         List<String> cc,
                         List<String> bcc,
                         String subject,
                         String text,
                         boolean isHtml,
                         List<Attachment> attachments) {
        this.recipients = recipients == null ? new ArrayList<>() : recipients;
        this.cc = cc == null ? new ArrayList<>() : cc;
        this.bcc = bcc == null ? new ArrayList<>() : bcc;
        this.subject = subject;
        this.text = text;
        this.isHtml = isHtml;
        this.attachments = attachments == null ? new ArrayList<>() : attachments;
    }

    public void markAsSent() {
        this.sentAt = Instant.now();
    }
}
