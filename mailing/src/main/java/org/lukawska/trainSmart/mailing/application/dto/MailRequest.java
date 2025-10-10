package org.lukawska.trainSmart.mailing.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
public class MailRequest {

	@NotNull
	@Size(min = 1, message = "At least one recipient required.")
	private String[] recipients;

	private String subject;

	private String text;

	private boolean isHtml;

	@Builder.Default
	private List<Attachment> attachments = List.of();

}
