package org.lukawska.trainSmart.mailing.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainSmart.mailing.application.dto.MailResponse;
import org.lukawska.trainSmart.mailing.application.exception.ExceptionType;
import org.lukawska.trainSmart.mailing.application.mapper.MailMapper;
import org.lukawska.trainSmart.mailing.application.validation.AttachmentValidation;
import org.lukawska.trainSmart.mailing.domain.entities.MailEntity;
import org.lukawska.trainSmart.mailing.domain.repository.MailRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {

	@Mock
	private MailRepository mailRepository;

	@Mock
	private MailSender mailSender;

	@Mock
	private MailMapper mailMapper;

	@Mock
	private AttachmentValidation attachmentValidation;

	@InjectMocks
	private MailService mailService;


	@Test
	void shouldGetMailByIdSuccess() {
		//given
		final Long id = 1L;
		MailEntity mailEntity = MailTestData.randomMailEntity(1, 1, 1);
		MailResponse expectedResponse = MailTestData.mapToResponse(mailEntity);

		when(mailRepository.findById(id)).thenReturn(Optional.of(mailEntity));
		when(mailMapper.mapToResponse(mailEntity)).thenReturn(expectedResponse);

		//when
		MailResponse actualResponse = mailService.getMailById(id);

		//then
		assertThat(actualResponse.id().equals(expectedResponse.id()));
	}

	@Test
	void shouldThrowExceptionWhenMailNotFound() {
		//given
		final Long id = 1L;
		when(mailRepository.findById(id)).thenReturn(Optional.empty());

		//when && then
		assertThatThrownBy(() -> mailService.getMailById(id))
				.isInstanceOf(RuntimeException.class)
				.hasMessage(ExceptionType.MAIL_NOT_FOUND.getMessage());
	}

	@Test
	void shouldGetAllMails() {
		//given
		final MailEntity mailEntity = MailTestData.randomMailEntity(1, 1, 1);
		final List<MailEntity> mailEntities = List.of(mailEntity);

		final MailResponse response = MailTestData.mapToResponse(mailEntity);
		final List<MailResponse> expectedResponses = List.of(response);

		when(mailRepository.findAll()).thenReturn(mailEntities);
		when(mailMapper.mapToResponse(mailEntity)).thenReturn(response);

		//when
		final List<MailResponse> actualResponses = mailService.getAllMails();

		//then
		assertEquals(expectedResponses, actualResponses);
	}

	@Test
	void shouldGetAllMailsByRecipient() {
		//given
		final MailEntity mailEntity = MailTestData.randomMailEntity(1, 1, 1);
		final String recipient = mailEntity.getRecipients().getFirst();

		final MailResponse response = MailTestData.mapToResponse(mailEntity);
		final List<MailResponse> expectedResponse = List.of(response);

		when(mailRepository.findAllByRecipient(recipient)).thenReturn(List.of(mailEntity));
		when(mailMapper.mapToResponse(mailEntity)).thenReturn(response);

		//when
		final List<MailResponse> actualResponse = mailService.getAllMailsByRecipient(recipient);

		//then
		assertEquals(expectedResponse, actualResponse);
	}

	@Test
	void shouldGetAllMailsByCc() {
		//given
		final MailEntity mailEntity = MailTestData.randomMailEntity(1, 1, 1);
		final List<MailEntity> mailEntities = List.of(mailEntity);
		final String cc = mailEntity.getCc().getFirst();

		final MailResponse response = MailTestData.mapToResponse(mailEntity);
		final List<MailResponse> expectedResponse = List.of(response);

		when(mailRepository.findAllByCcContaining(cc)).thenReturn(mailEntities);
		when(mailMapper.mapToResponse(mailEntity)).thenReturn(response);

		//when
		List<MailResponse> actualResponse = mailService.getAllMailsByCc(cc);

		//then
		assertEquals(expectedResponse, actualResponse);
	}

	@Test
	void shouldGetAllMailsByBcc() {
		//given
		final MailEntity mailEntity = MailTestData.randomMailEntity(1, 1, 1);
		final List<MailEntity> mailEntities = List.of(mailEntity);
		final String bcc = mailEntity.getBcc().getFirst();

		final MailResponse response = MailTestData.mapToResponse(mailEntity);
		final List<MailResponse> expectedResponse = List.of(response);

		when(mailMapper.mapToResponse(mailEntity)).thenReturn(response);
		when(mailRepository.findAllByBccContaining(bcc)).thenReturn(mailEntities);

		//when
		List<MailResponse> actualResponse = mailService.getAllMailsByBcc(bcc);

		//then
		assertEquals(expectedResponse, actualResponse);
	}

	@Test
	void shouldGetAllMailsBySentAtBetween() {
		//given
		final MailEntity mailEntity = MailTestData.randomMailEntity(1, 1, 1);
		final List<MailEntity> mailEntities = List.of(mailEntity);

		final MailResponse response = MailTestData.mapToResponse(mailEntity);
		final List<MailResponse> expectedResponse = List.of(response);
		final String from = "2024-01-01T00:00:00Z";
		final String to = "2024-12-31T23:59:59Z";

		when(mailRepository.findAllBySentAtBetween(Instant.parse(from), Instant.parse(to))).thenReturn(mailEntities);
		when(mailMapper.mapToResponse(mailEntity)).thenReturn(response);

		//when
		List<MailResponse> actualResponse = mailService.getAllMailsBySentAtBetween(from, to);

		//then
		assertEquals(expectedResponse, actualResponse);
	}

	@Test
	void shouldGetAllMailsBySubjectContaining() {
		//given
		final MailEntity mailEntity = MailTestData.randomMailEntity(1, 1, 1);
		final List<MailEntity> mailEntities = List.of(mailEntity);

		final MailResponse response = MailTestData.mapToResponse(mailEntity);
		final List<MailResponse> expectedResponse = List.of(response);

		when(mailRepository.findAllBySubjectContaining(any())).thenReturn(mailEntities);
		when(mailMapper.mapToResponse(mailEntity)).thenReturn(response);

		//when
		List<MailResponse> actualResponse = mailService.getAllMailsBySubjectContaining(mailEntity.getSubject());

		//then
		assertEquals(expectedResponse, actualResponse);
	}

	@Test
	void shouldGetAllMailsByIsHtml() {
		//given
		final MailEntity mailEntity = MailTestData.randomMailEntity(1, 1, 1);
		final List<MailEntity> mailEntities = List.of(mailEntity);

		final MailResponse response = MailTestData.mapToResponse(mailEntity);
		final List<MailResponse> expectedResponse = List.of(response);

		when(mailRepository.findAllByIsHtml(mailEntity.isHtml())).thenReturn(mailEntities);
		when(mailMapper.mapToResponse(mailEntity)).thenReturn(response);

		//when
		List<MailResponse> actualResponse = mailService.getAllMailsByIsHtml(mailEntity.isHtml());

		//then
		assertEquals(expectedResponse, actualResponse);
	}
}
