package org.lukawska.trainSmart.mailing.application.validation;

import org.lukawska.trainSmart.mailing.domain.valueObject.Attachment;

import java.io.IOException;
import java.util.List;

public interface AttachmentValidator {

    void validateAttachments(List<Attachment> attachments) throws IOException;

}
