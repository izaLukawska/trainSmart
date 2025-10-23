package org.lukawska.trainSmart.mailing.application.validation;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.lukawska.trainSmart.mailing.application.exception.ExceptionType;
import org.lukawska.trainSmart.mailing.application.exception.MailingException;
import org.lukawska.trainSmart.mailing.domain.valueObject.Attachment;
import org.lukawska.trainSmart.mailing.domain.valueObject.AttachmentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Component
public class AttachmentValidator {

    private final long maxAttachmentSize;

    public AttachmentValidator(@Value("${mail.attachments.max-size}") long maxAttachmentSize) {
        this.maxAttachmentSize = maxAttachmentSize;
    }

    public void validateAttachments(List<Attachment> attachments) {
        for (Attachment att : attachments) {
            validateAttachmentType(att);
            validateAttachmentFileName(att);
            validateAttachmentExtension(att);
            validateAttachmentSize(att);
        }
    }

    private void validateAttachmentType(Attachment att) {
        if (att.attachmentType() == null) {
            throw new MailingException(ExceptionType.MISSING_ATTACHMENT);
        }
    }

    private void validateAttachmentFileName(Attachment att) {
        if (StringUtils.isBlank(att.fileName())) {
            throw new MailingException(ExceptionType.INVALID_ATTACHMENT_NAME);
        }
    }

    private void validateAttachmentExtension(Attachment att) {
        String extension = FilenameUtils.getExtension(att.fileName());
        if (StringUtils.isBlank(extension) || Arrays.stream(AttachmentType.values())
                                                    .map(AttachmentType::getExtension)
                                                    .noneMatch(ext -> ext.equalsIgnoreCase(extension))) {
            throw new MailingException(ExceptionType.INVALID_ATTACHMENT_EXTENSION);
        }
    }

    private void validateAttachmentSize(Attachment att) {
        try {
            InputStream in = att.source().getInputStream();
            byte[] buffer = new byte[8192];
            long total = 0;
            int read;
            while ((read = in.read(buffer)) != -1) {
                total += read;
                if (total > maxAttachmentSize) {
                    throw new MailingException(ExceptionType.ATTACHMENT_TOO_LARGE);
                }
            }
        } catch (IOException e) {
            throw new MailingException(ExceptionType.ATTACHMENT_IO_ERROR);
        }
    }
}
