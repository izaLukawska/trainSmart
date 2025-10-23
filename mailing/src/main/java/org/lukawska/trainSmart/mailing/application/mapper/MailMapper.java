package org.lukawska.trainSmart.mailing.application.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.lukawska.trainSmart.mailing.application.dto.MailRequest;
import org.lukawska.trainSmart.mailing.application.dto.MailResponse;
import org.lukawska.trainSmart.mailing.domain.entities.MailEntity;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MailMapper {

    public static MailEntity mapToEntity(MailRequest mailRequest) {
        return MailEntity.builder()
                         .recipients(mailRequest.recipients())
                         .cc(defaultListIfNull(mailRequest.cc()))
                         .bcc(defaultListIfNull(mailRequest.bcc()))
                         .subject(mailRequest.subject())
                         .text(mailRequest.text())
                         .isHtml(mailRequest.isHtml())
                         .build();
    }

    public static MailResponse mapToResponse(MailEntity mail, String mailFrom, String replyTo) {
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
