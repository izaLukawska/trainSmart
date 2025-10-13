package org.lukawska.trainSmart.mailing.application.mapper;

import lombok.NoArgsConstructor;
import org.lukawska.trainSmart.mailing.application.dto.MailRequest;
import org.lukawska.trainSmart.mailing.application.dto.MailResponse;
import org.lukawska.trainSmart.mailing.domain.entities.MailEntity;

import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class MailMapper {

	public static MailEntity mapToEntity(MailRequest mailRequest) {
		return MailEntity.builder()
		                 .recipients(mailRequest.recipients())
		                 .cc(defaultListIfNull(mailRequest.cc()))
		                 .bcc(defaultListIfNull(mailRequest.bcc()))
		                 .subject(mailRequest.subject())
		                 .body(mailRequest.body())
		                 .isHtml(mailRequest.isHtml())
		                 .attachmentList(defaultListIfNull(mailRequest.attachmentList()))
		                 .build();
	}

	public static MailResponse mapToResponse(MailEntity mail) {
		return new MailResponse(
				mail.getId(),
				defaultListIfNull(mail.getRecipients()),
				defaultListIfNull(mail.getCc()),
				defaultListIfNull(mail.getBcc()),
				mail.getSubject(),
				mail.getBody(),
				mail.isHtml(),
				mail.getFrom(),
				mail.getReplyTo(),
				defaultListIfNull(mail.getAttachmentList()),
				mail.getSentAt(),
				mail.isSentSuccess()
		);
	}

	private static <T> List<T> defaultListIfNull(List<T> list) {
		return list == null ? List.of() : list;
	}
}
