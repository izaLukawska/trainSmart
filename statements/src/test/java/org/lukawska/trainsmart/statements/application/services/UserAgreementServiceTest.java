package org.lukawska.trainsmart.statements.application.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementRequest;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementResponse;
import org.lukawska.trainsmart.statements.application.exception.ExceptionType;
import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.lukawska.trainsmart.statements.application.mapper.UserAgreementMapper;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.lukawska.trainsmart.statements.domain.repositories.UserAgreementRepository;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;
import org.lukawska.trainsmart.statements.infra.config.Statement;
import org.lukawska.trainsmart.statements.infra.config.StatementsDefinition;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAgreementServiceTest {

	@Mock
	private UserAgreementRepository repository;

	@Mock
	private StatementsDefinition definitions;

	@Mock
	private UserAgreementMapper mapper;

	@InjectMocks
	private UserAgreementService service;

	@Test
	void shouldReturnUpdatedAgreementWhenFound() {
		//given
		final Long userId = new Random().nextLong();
		final String statementCode = UUID.randomUUID().toString();
		final AgreementStatus status = AgreementStatus.ACCEPTED;
		final Statement statement = new Statement(statementCode, 1, false, UUID.randomUUID().toString());
		final UserAgreementRequest request = new UserAgreementRequest(userId, statementCode, status);
		final UserAgreement userAgreement = new UserAgreement(userId, statementCode, AgreementStatus.REJECTED);
		final UserAgreementResponse expectedResponse = new UserAgreementResponse(1L,
		                                                                         userId,
		                                                                         statementCode,
		                                                                         1,
		                                                                         status);

		when(repository.findByUserIdAndStatementCode(userId, statementCode)).thenReturn(Optional.of(userAgreement));
		when(definitions.findStatementByCode(statementCode)).thenReturn(Optional.of(statement));
		when(repository.save(any(UserAgreement.class))).thenAnswer(invocation -> invocation.getArgument(0));
		doReturn(expectedResponse).when(mapper).mapToResponse(any(UserAgreement.class));

		//when
		UserAgreementResponse actualResponse = service.signAgreement(request);

		//then
		assertThat(actualResponse).isEqualTo(expectedResponse);
	}

	@Test
	void shouldReturnUnchangedAgreementWhenFound() {
		//given
		final Long userId = new Random().nextLong();
		final String statementCode = UUID.randomUUID().toString();
		final AgreementStatus status = AgreementStatus.ACCEPTED;
		final Statement statement = new Statement(statementCode, 1, false, UUID.randomUUID().toString());
		final UserAgreementRequest request = new UserAgreementRequest(userId, statementCode, status);
		final UserAgreement userAgreement = new UserAgreement(userId, statementCode, status);
		final UserAgreementResponse expectedResponse = new UserAgreementResponse(1L,
		                                                                         userId,
		                                                                         statementCode,
		                                                                         1,
		                                                                         status);

		when(repository.findByUserIdAndStatementCode(userId, statementCode)).thenReturn(Optional.of(userAgreement));
		when(definitions.findStatementByCode(statementCode)).thenReturn(Optional.of(statement));
		when(mapper.mapToResponse(any(UserAgreement.class))).thenReturn(expectedResponse);

		//when
		UserAgreementResponse actualResponse = service.signAgreement(request);

		//then
		assertThat(actualResponse).isEqualTo(expectedResponse);
	}

	@Test
	void shouldReturnNewAgreementWhenAgreementNotFound() {
		//given
		final Long userId = new Random().nextLong();
		final String statementCode = UUID.randomUUID().toString();
		final AgreementStatus status = AgreementStatus.ACCEPTED;
		final Statement statement = new Statement(statementCode, 1, false, UUID.randomUUID().toString());
		final UserAgreementRequest request = new UserAgreementRequest(userId, statementCode, status);
		final UserAgreement userAgreement = new UserAgreement(userId, statementCode, status);
		final UserAgreementResponse expectedResponse = new UserAgreementResponse(1L,
		                                                                         userId,
		                                                                         statementCode,
		                                                                         1,
		                                                                         status);
		when(definitions.findStatementByCode(statementCode)).thenReturn(Optional.of(statement));
		when(repository.findByUserIdAndStatementCode(userId, statementCode)).thenReturn(Optional.empty());
		doReturn(userAgreement).when(mapper).mapToEntity(any(UserAgreementRequest.class));
		when(repository.save(any(UserAgreement.class))).thenReturn(userAgreement);
		doReturn(expectedResponse).when(mapper).mapToResponse(any(UserAgreement.class));

		// when
		UserAgreementResponse actualResponse = service.signAgreement(request);

		// then
		assertThat(actualResponse).isEqualTo(expectedResponse);
	}

	@Test
	void shouldReturnEmptyListWhenRequiredUserAgreementToSignNotFound() {
		//given
		final Long userId = new Random().nextLong();
		final String statementCode = UUID.randomUUID().toString();
		final AgreementStatus status = AgreementStatus.ACCEPTED;
		final UserAgreement userAgreement = new UserAgreement(userId, statementCode, status);
		when(repository.findAllByUserId(userId)).thenReturn(List.of(userAgreement));
		when(definitions.getRequiredStatementsMap()).thenReturn(Map.of());

		//when && then
		assertThat(service.getRequiredStatementsToSign(userId)).isEmpty();
	}

	@Test
	void shouldReturnRequiredUserAgreementToSignWhenFound() {
		//given
		final Long userId = new Random().nextLong();
		final String statementCode = UUID.randomUUID().toString();
		final AgreementStatus status = AgreementStatus.ACCEPTED;
		final UserAgreement userAgreement = new UserAgreement(userId, statementCode, status);
		final Statement required = new Statement(statementCode, 2, true, UUID.randomUUID().toString());
		final UserAgreementResponse expectedUa = new UserAgreementResponse(1L,
		                                                                   userId,
		                                                                   statementCode,
		                                                                   1,
		                                                                   status);

		when(repository.findAllByUserId(userId)).thenReturn(List.of(userAgreement));
		when(definitions.getRequiredStatementsMap()).thenReturn(Map.of(statementCode, required));
		doReturn(expectedUa).when(mapper).mapToResponse(any(UserAgreement.class));

		//when && then
		assertThat(service.getRequiredStatementsToSign(userId))
				.hasSize(1)
				.contains(expectedUa);
	}

	@Test
	void shouldThrowExceptionWhenNoAgreementsAreFound() {
		//given
		final Long userId = new Random().nextLong();
		when(repository.findAllByUserId(userId)).thenReturn(List.of());

		//when && then
		assertThatThrownBy(() -> service.getRequiredStatementsToSign(userId))
				.isInstanceOf(StatementException.class)
				.hasMessage(ExceptionType.USER_NOT_FOUND.getMessage());
	}

	@Test
	void shouldThrowExceptionWhenStatementNotFound() {
		//given
		final String statementCode = UUID.randomUUID().toString();
		final Long userId = new Random().nextLong();
		final UserAgreementRequest request = new UserAgreementRequest(userId, statementCode, AgreementStatus.ACCEPTED);
		when(definitions.findStatementByCode(statementCode)).thenReturn(Optional.empty());

		//when && then
		assertThatThrownBy(() -> service.signAgreement(request))
				.isInstanceOf(StatementException.class)
				.hasMessage(ExceptionType.STATEMENT_NOT_FOUND.getMessage());
	}

	@Test
	void shouldThrowExceptionWhenStatementRequiredButRejected() {
		//given
		final String statementCode = UUID.randomUUID().toString();
		final UserAgreementRequest request = new UserAgreementRequest(new Random().nextLong(), statementCode,
		                                                              AgreementStatus.REJECTED);
		final Statement statement = new Statement(UUID.randomUUID().toString(),
		                                          new Random().nextInt(),
		                                          true,
		                                          UUID.randomUUID().toString());
		when(definitions.findStatementByCode(statementCode)).thenReturn(Optional.of(statement));

		//when && then
		assertThatThrownBy(() -> service.signAgreement(request))
				.isInstanceOf(StatementException.class)
				.hasMessage(ExceptionType.STATEMENT_ACCEPTANCE_REQUIRED.getMessage());
	}
}
