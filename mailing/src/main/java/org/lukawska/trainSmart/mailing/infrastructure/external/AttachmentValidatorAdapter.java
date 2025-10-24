package org.lukawska.trainSmart.mailing.infrastructure.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.lukawska.trainSmart.mailing.application.exception.ExceptionType;
import org.lukawska.trainSmart.mailing.application.exception.MailingException;
import org.lukawska.trainSmart.mailing.application.validation.AttachmentValidator;
import org.lukawska.trainSmart.mailing.domain.valueObject.Attachment;
import org.lukawska.trainSmart.mailing.infrastructure.config.MailingProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AttachmentValidatorAdapter implements AttachmentValidator {

    private final MailingProperties properties;

    private final Tika tika;

    @Override
    public void validateAttachments(List<Attachment> attachments) {
        long maxSizeBytes = properties.getMaxSizeBytes();

        for (Attachment att : attachments) {
            validateExtension(att);
            validateSize(att, maxSizeBytes);
            validateMimeType(att);
        }
    }

    private void validateExtension(Attachment attachment) {
        String fileName = attachment.getFileName().toLowerCase();
        String ext = FilenameUtils.getExtension(fileName);
        if (!properties.getMimeTypesByExt().containsKey(ext)) {
            log.warn("Attachment '{}' has unsupported extension: {}", fileName, ext);
            throw new MailingException(ExceptionType.INVALID_ATTACHMENT_EXTENSION);
        }
    }

    private void validateSize(Attachment attachment, long maxSizeBytes) {
        if (attachment.getSize() > maxSizeBytes) {
            throw new MailingException(ExceptionType.ATTACHMENT_TOO_LARGE);
        }
    }

    private void validateMimeType(Attachment attachment) {
        String fileName = attachment.getFileName().toLowerCase();
        String ext = FilenameUtils.getExtension(fileName);
        List<String> mimeTypes = properties.getMimeTypesByExt().get(ext);

        String detected = tika.detect(attachment.getContent(), fileName);
        if (CollectionUtils.isEmpty(mimeTypes) || !mimeTypes.contains(detected)) {
            log.warn("Attachment '{}' has invalid MIME type: {}", fileName, detected);
            throw new MailingException(ExceptionType.INVALID_ATTACHMENT_TYPE);
        }
    }
}
