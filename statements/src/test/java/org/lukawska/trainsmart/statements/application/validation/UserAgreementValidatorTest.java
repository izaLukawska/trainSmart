package org.lukawska.trainsmart.statements.application.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementRequest;
import org.lukawska.trainsmart.statements.application.exception.ExceptionType;
import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.lukawska.trainsmart.statements.domain.repositories.UserAgreementRepository;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;
import org.lukawska.trainsmart.statements.infra.config.Statement;
import org.lukawska.trainsmart.statements.infra.config.StatementsDefinition;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.lukawska.trainsmart.statements.testutil.StatementTestData.*;
import static org.lukawska.trainsmart.statements.testutil.UserAgreementTestData.acceptedUserAgreementRequest;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAgreementValidatorTest {

    @Mock
    private StatementsDefinition statementsDefinition;

    @Mock
    private UserAgreementRepository userAgreementRepository;

    @InjectMocks
    private UserAgreementValidator userAgreementValidator;

    @Test
    void shouldValidateUserAgreementSuccess() {
        //given
        final UserAgreementRequest request = acceptedUserAgreementRequest();
        final Statement expected = optionalStatement();

        when(statementsDefinition.findStatementByCode(request.statementCode()))
                .thenReturn(Optional.of(expected));

        //when
        Statement actualStatement = userAgreementValidator.validateUserAgreement(request);

        //then
        assertThat(actualStatement.title()).isEqualTo(expected.title());
        assertThat(actualStatement.version()).isEqualTo(expected.version());
        assertThat(actualStatement.required()).isFalse();
    }

    @Test
    void shouldValidateNewUserAgreementSuccess() {
        //given
        final UserAgreementRequest request = acceptedUserAgreementRequest();
        final Statement expected = requiredStatement();

        when(statementsDefinition.findStatementByCode(request.statementCode()))
                .thenReturn(Optional.of(expected));
        when(userAgreementRepository.findByUserIdAndStatementCode(request.userId(), request.statementCode()))
                .thenReturn(Optional.empty());

        //when
        Statement actualStatement = userAgreementValidator.validateNewUserAgreement(request);

        //then
        assertThat(actualStatement.title()).isEqualTo(expected.title());
        assertThat(actualStatement.version()).isEqualTo(expected.version());
        assertThat(actualStatement.required()).isTrue();
    }

    @Test
    void shouldReturnTrueWhenUserAgreementOutdated() {
        //given
        final String statementCode = randomStatementCode();
        when(statementsDefinition.getRequiredStatementsMap()).thenReturn(Map.of(statementCode, requiredStatement(2)));
        final UserAgreement existingUserAgreement = new UserAgreement(mock(User.class),
                                                                      statementCode,
                                                                      1,
                                                                      AgreementStatus.ACCEPTED);
        //when && then
        assertThat(userAgreementValidator.outdatedUserAgreement(existingUserAgreement)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenUserAgreementUpToDate() {
        //given
        final String statementCode = randomStatementCode();
        when(statementsDefinition.getRequiredStatementsMap()).thenReturn(Map.of(statementCode, requiredStatement(2)));
        final UserAgreement existingUserAgreement = new UserAgreement(mock(User.class),
                                                                      statementCode,
                                                                      2,
                                                                      AgreementStatus.ACCEPTED);
        //when && then
        assertThat(userAgreementValidator.outdatedUserAgreement(existingUserAgreement)).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenUserAgreementAlreadyExists() {
        //given
        final UserAgreementRequest request = acceptedUserAgreementRequest();
        final UserAgreement existingUserAgreement = new UserAgreement(mock(User.class),
                                                                      request.statementCode(),
                                                                      1,
                                                                      AgreementStatus.ACCEPTED);

        when(statementsDefinition.findStatementByCode(request.statementCode()))
                .thenReturn(Optional.of(optionalStatement()));
        when(userAgreementRepository.findByUserIdAndStatementCode(request.userId(), request.statementCode()))
                .thenReturn(Optional.of(existingUserAgreement));

        //when && then
        assertThatThrownBy(() -> userAgreementValidator.validateNewUserAgreement(request))
                .isInstanceOf(StatementException.class)
                .hasMessage(ExceptionType.USER_AGREEMENT_ALREADY_EXISTS.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenStatementRequiredButRejected() {
        //given
        final UserAgreementRequest request = new UserAgreementRequest(new Random().nextLong(),
                                                                      randomStatementCode(),
                                                                      AgreementStatus.REJECTED);

        when(statementsDefinition.findStatementByCode(request.statementCode()))
                .thenReturn(Optional.of(requiredStatement()));

        //when && then
        assertThatThrownBy(() -> userAgreementValidator.validateUserAgreement(request))
                .isInstanceOf(StatementException.class)
                .hasMessage(ExceptionType.STATEMENT_ACCEPTANCE_REQUIRED.getMessage());
    }
}
