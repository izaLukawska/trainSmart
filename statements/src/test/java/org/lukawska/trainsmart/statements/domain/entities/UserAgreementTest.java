package org.lukawska.trainsmart.statements.domain.entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.lukawska.trainsmart.statements.application.exception.ExceptionType;
import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;

import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserAgreementTest {

	@Test
	void shouldUpdateStatusSuccess() {
		//given
		UserAgreement ua = new UserAgreement(new Random().nextLong(),
		                                     UUID.randomUUID().toString(),
		                                     AgreementStatus.REJECTED);

		//when
		ua.changeStatus(AgreementStatus.ACCEPTED);

		//then
		assertThat(ua.getStatus().equals(AgreementStatus.ACCEPTED));
	}

	@ParameterizedTest
	@NullSource
	void shouldThrowExceptionWhenStatusIsNull(AgreementStatus status) {
		// given
		AgreementStatus initial = AgreementStatus.REJECTED;
		UserAgreement ua = new UserAgreement(new Random().nextLong(), UUID.randomUUID().toString(), initial);

		// when / then
		assertThatThrownBy(() -> ua.changeStatus(status))
				.isInstanceOf(StatementException.class)
				.hasMessage(ExceptionType.INVALID_STATUS.getMessage());
	}

	@Test
	void shouldUpdateStatementVersion() {
		final int version = new Random().nextInt();
		UserAgreement ua = new UserAgreement(new Random().nextLong(),
		                                     UUID.randomUUID().toString(),
		                                     AgreementStatus.ACCEPTED);
		ua.updateStatementVersion(version);
		assertEquals(version, ua.getStatementVersion());
	}
}
