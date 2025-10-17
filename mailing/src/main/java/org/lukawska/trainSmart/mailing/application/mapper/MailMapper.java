package org.lukawska.trainSmart.mailing.application.mapper;

import org.lukawska.trainSmart.mailing.application.dto.MailRequest;
import org.lukawska.trainSmart.mailing.application.dto.MailResponse;
import org.lukawska.trainSmart.mailing.domain.entities.MailEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class MailMapper {

	@Value("${mail.from}")
	private String mailFrom;

	@Value("${mail.reply-to}")
	private String replyTo;

	public MailEntity mapToEntity(MailRequest mailRequest) {
		return MailEntity.builder()
		                 .recipients(mailRequest.recipients())
		                 .cc(defaultListIfNull(mailRequest.cc()))
		                 .bcc(defaultListIfNull(mailRequest.bcc()))
		                 .subject(mailRequest.subject())
		                 .body(mailRequest.body())
		                 .isHtml(mailRequest.isHtml())
		                 .build();
	}

	public MailResponse mapToResponse(MailEntity mail) {
		return new MailResponse(mail.getId(),
		                        defaultListIfNull(mail.getRecipients()),
		                        defaultListIfNull(mail.getCc()),
		                        defaultListIfNull(mail.getBcc()),
		                        mail.getSubject(),
		                        mailFrom,
		                        replyTo,
		                        mail.getSentAt());
	}

	private static <T> List<T> defaultListIfNull(List<T> list) {
		return list == null ? List.of() : list;
	}
}
