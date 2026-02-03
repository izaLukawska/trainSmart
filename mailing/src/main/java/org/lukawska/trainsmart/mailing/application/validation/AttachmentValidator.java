package org.lukawska.trainsmart.mailing.application.validation;

import org.lukawska.trainsmart.mailing.domain.valueObjects.Attachment;

import java.io.IOException;
import java.util.List;

public interface AttachmentValidator {

    void validateAttachments(List<Attachment> attachments) throws IOException;

}
