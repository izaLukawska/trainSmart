package org.lukawska.trainSmart.mailing.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Table(name = "mails")
public class MailEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Builder.Default
	@ElementCollection
	@CollectionTable(name = "mail_recipients", joinColumns = @JoinColumn(name = "mail_id"))
	@Column(name = "recipient")
	private List<String> recipients = new ArrayList<>();

	@ElementCollection
	@Builder.Default
	@CollectionTable(name = "mail_cc", joinColumns = @JoinColumn(name = "mail_id"))
	@Column(name = "cc")
	private List<String> cc = new ArrayList<>();

	@ElementCollection
	@Builder.Default
	@CollectionTable(name = "mail_bcc", joinColumns = @JoinColumn(name = "mail_id"))
	@Column(name = "bcc")
	private List<String> bcc = new ArrayList<>();

	private String subject;

	@Lob
	private String body;

	private boolean isHtml;

	private Instant sentAt;

	public void markAsSent() {
		this.sentAt = Instant.now();
	}

}
