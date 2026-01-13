package org.lukawska.trainsmart.mailing.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.mailing.application.dto.AttachmentMeta;
import org.lukawska.trainsmart.mailing.application.dto.MailDetails;
import org.lukawska.trainsmart.mailing.application.dto.MailResponse;
import org.lukawska.trainsmart.mailing.domain.entities.MailEntity;
import org.lukawska.trainsmart.mailing.domain.valueObjects.Attachment;

import java.util.List;

@UtilityClass
public final class MailMapper {

    public static MailEntity mapToEntity(MailDetails mailDetails) {
        return MailEntity.builder()
                         .recipients(mailDetails.recipients())
                         .cc(defaultListIfNull(mailDetails.cc()))
                         .bcc(defaultListIfNull(mailDetails.bcc()))
                         .subject(mailDetails.subject())
                         .text(mailDetails.text())
                         .isHtml(mailDetails.isHtml())
                         .attachments(defaultListIfNull(mailDetails.attachments()))
                         .build();
    }

    public static MailResponse mapToResponse(MailEntity mail, String mailFrom, String replyTo) {
        return new MailResponse(mail.getId(),
                                defaultListIfNull(mail.getRecipients()),
                                defaultListIfNull(mail.getCc()),
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
