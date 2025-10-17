package org.lukawska.trainSmart.mailing.application.service;

import lombok.NoArgsConstructor;
import org.lukawska.trainSmart.mailing.application.dto.MailResponse;
import org.lukawska.trainSmart.mailing.domain.entities.MailEntity;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
final class MailTestData {

	private static final Random RANDOM = new Random(1L);

	static MailEntity randomMailEntity(int recipientCount, int ccCount, int bccCount) {
		return MailEntity.builder()
		                 .id(RANDOM.nextLong(1, 10))
		                 .recipients(randomEmails(recipientCount))
		                 .cc(randomEmails(ccCount))
		                 .bcc(randomEmails(bccCount))
		                 .subject(randomSubject())
		                 .body(randomBody())
		                 .isHtml(RANDOM.nextBoolean())
		                 .sentAt(Instant.now().minusSeconds(RANDOM.nextInt(3600)))
		                 .build();
	}

	static MailResponse mapToResponse(MailEntity entity) {
		return new MailResponse(entity.getId(),
		                        entity.getRecipients(),
		                        entity.getCc(),
		                        entity.getBcc(),
		                        entity.getSubject(),
		                        randomEmail(),
		                        randomEmail(),
		                        entity.getSentAt());
	}

	static List<String> randomEmails(int count) {
		return IntStream.range(0, count).mapToObj(i -> randomEmail()).toList();
	}

	static String randomEmail() {
		return "user" + RANDOM.nextInt(1000) + "@example.com";
	}

	static String randomSubject() {
		return "Subject " + UUID.randomUUID();
	}

	static String randomBody() {
		return "Body " + UUID.randomUUID();
	}
}
