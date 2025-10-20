package org.lukawska.trainsmart.statements.presentation.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementRequest;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementResponse;
import org.lukawska.trainsmart.statements.application.services.UserAgreementService;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAgreementControllerTest {

	@Mock
	private UserAgreementService service;

	@InjectMocks
	private UserAgreementController controller;

	@Test
	void shouldReturnResponseWhenSignAgreementSuccess() {
		//given
		UserAgreementRequest request = mock(UserAgreementRequest.class);
		final Long userId = new Random().nextLong();
		final String statementCode = UUID.randomUUID().toString();
		final AgreementStatus status = AgreementStatus.ACCEPTED;

		when(request.userId()).thenReturn(userId);
		when(request.statementCode()).thenReturn(statementCode);
		when(request.status()).thenReturn(status);

		UserAgreementResponse expectedResponse = mock(UserAgreementResponse.class);
		when(service.signAgreement(userId, statementCode, status)).thenReturn(expectedResponse);

		//when
		ResponseEntity<UserAgreementResponse> responseEntity = controller.signAgreement(request);

		//then
		assertThat(responseEntity.getBody()).isNotNull().isEqualTo(expectedResponse);
	}

	@Test
	void shouldReturnRequiredStatementsListWhenFound() {
		//given
		final Long userId = new Random().nextLong();
		List<UserAgreementResponse> expectedList = List.of(mock(UserAgreementResponse.class));
		when(service.getRequiredStatementsToSign(userId)).thenReturn(expectedList);

		//when
		List<UserAgreementResponse> actualList = controller.getRequiredStatementsToSign(userId);

		//then
		assertThat(expectedList.equals(actualList));
	}

	@ParameterizedTest
	@EmptySource
	void shouldReturnEmptyRequiredStatementsList(List<UserAgreementResponse> emptyList) {
		//given
		final Long userId = new Random().nextLong();
		when(service.getRequiredStatementsToSign(userId)).thenReturn(emptyList);

		//when
		List<UserAgreementResponse> actualList = controller.getRequiredStatementsToSign(userId);

		//then
		assertThat(actualList).isEmpty();
	}
}
