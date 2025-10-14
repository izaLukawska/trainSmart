package org.lukawska.trainSmart.mailing.domain.valueObject;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AttachmentType {

	PDF("application/pdf", "pdf"),
	EXCEL("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");

	private final String mimeType;
	private final String extension;

}
