package org.lukawska.trainSmart.mailing.application.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.lukawska.trainSmart.mailing.application.dto.AttachmentMeta;
import org.lukawska.trainSmart.mailing.application.dto.MailRequest;
import org.lukawska.trainSmart.mailing.application.dto.MailResponse;
import org.lukawska.trainSmart.mailing.domain.entities.MailEntity;
import org.lukawska.trainSmart.mailing.domain.valueObject.Attachment;

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
                         .attachments(mailRequest.attachments())
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
                                mapToAttachmentMeta(mail.getAttachments()),
                                mail.getSentAt());
    }

    private static List<AttachmentMeta> mapToAttachmentMeta(List<Attachment> attachments) {
        return defaultListIfNull(attachments)
                .stream()
                .map(att -> new AttachmentMeta(att.getFileName(), att.getSize()))
                .toList();
    }

    private static <T> List<T> defaultListIfNull(List<T> list) {
        return list == null ? List.of() : list;
    }
}
