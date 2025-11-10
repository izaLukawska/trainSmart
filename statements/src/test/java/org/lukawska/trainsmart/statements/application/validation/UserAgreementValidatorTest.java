package org.lukawska.trainsmart.statements.application.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementRequest;
import org.lukawska.trainsmart.statements.application.exception.ExceptionType;
import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.lukawska.trainsmart.statements.infra.config.Statement;
import org.lukawska.trainsmart.statements.infra.config.StatementsDefinition;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.lukawska.trainsmart.statements.testutil.StatementTestData.*;
import static org.lukawska.trainsmart.statements.testutil.UserAgreementTestData.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAgreementValidatorTest {

    @Mock
    private StatementsDefinition statementsDefinition;

    @InjectMocks
    private UserAgreementValidator userAgreementValidator;

    @Test
    void shouldValidateUserAgreementSuccess() {
        //given
        final UserAgreementRequest request = acceptedUserAgreementRequest();
        final Statement expectedStatement = optionalStatement();

        when(statementsDefinition.findStatementByCode(request.statementCode()))
                .thenReturn(Optional.of(expectedStatement));

        //when
        Statement actualStatement = userAgreementValidator.validateUserAgreement(request);

        //then
        assertThat(actualStatement.title()).isEqualTo(expectedStatement.title());
        assertThat(actualStatement.version()).isEqualTo(expectedStatement.version());
        assertThat(actualStatement.required()).isFalse();
    }

    @Test
    void shouldReturnTrueWhenUserAgreementOutdated() {
        //given
        final String statementCode = randomStatementCode();
        when(statementsDefinition.getRequiredStatementsMap()).thenReturn(Map.of(statementCode, requiredStatement(2)));
        final UserAgreement existingUserAgreement = acceptedUserAgreement(statementCode, 1);

        //when && then
        assertThat(userAgreementValidator.outdatedUserAgreement(existingUserAgreement)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenUserAgreementUpToDate() {
        //given
        final String statementCode = randomStatementCode();
        when(statementsDefinition.getRequiredStatementsMap()).thenReturn(Map.of(statementCode, requiredStatement(2)));
        final UserAgreement existingUserAgreement = acceptedUserAgreement(statementCode, 2);

        //when && then
        assertThat(userAgreementValidator.outdatedUserAgreement(existingUserAgreement)).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenStatementRequiredButRejected() {
        //given
        final UserAgreementRequest request = rejectedUserAgreementRequest(randomStatementCode());

        when(statementsDefinition.findStatementByCode(request.statementCode()))
                .thenReturn(Optional.of(requiredStatement()));

        //when && then
        assertThatThrownBy(() -> userAgreementValidator.validateUserAgreement(request))
                .isInstanceOf(StatementException.class)
                .hasMessage(ExceptionType.STATEMENT_ACCEPTANCE_REQUIRED.getMessage());
    }
}
