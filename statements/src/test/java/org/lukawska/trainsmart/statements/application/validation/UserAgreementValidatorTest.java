package org.lukawska.trainsmart.statements.application.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.shared_persistence.domain.repositories.UserRepository;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementRequest;
import org.lukawska.trainsmart.statements.application.exception.ExceptionType;
import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.lukawska.trainsmart.statements.domain.repositories.UserAgreementRepository;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;
import org.lukawska.trainsmart.statements.infra.config.StatementsDefinition;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.lukawska.trainsmart.statements.testutil.StatementTestData.*;
import static org.lukawska.trainsmart.statements.testutil.UserAgreementTestData.randomUserAgreementRequest;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAgreementValidatorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserAgreementRepository userAgreementRepository;

    @Mock
    private StatementsDefinition statementsDefinition;

    @InjectMocks
    private UserAgreementValidator userAgreementValidator;

    @Test
    void shouldValidateStatementSuccess() {
        //given
        final UserAgreementRequest request = randomUserAgreementRequest();
        final String statementCode = request.statementCode();

        when(statementsDefinition.findStatementByCode(statementCode))
                .thenReturn(Optional.of(randomOptionalStatement()));
        when(userAgreementRepository.findByUserIdAndStatementCode(request.userId(), statementCode))
                .thenReturn(Optional.empty());

        //when && then
        assertThatCode(() -> userAgreementValidator.validateStatement(request)).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowExceptionWhenStatementRequiredButRejected() {
        //given
        final UserAgreementRequest request = new UserAgreementRequest(new Random().nextLong(),
                                                                      randomStatementCode(),
                                                                      AgreementStatus.REJECTED);

        when(statementsDefinition.findStatementByCode(request.statementCode()))
                .thenReturn(Optional.of(randomRequiredStatement()));

        //when && then
        assertThatThrownBy(() -> userAgreementValidator.validateStatement(request))
                .isInstanceOf(StatementException.class)
                .hasMessage(ExceptionType.STATEMENT_ACCEPTANCE_REQUIRED.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserAgreementExists() {
        //given
        final UserAgreementRequest request = randomUserAgreementRequest();
        final String statementCode = request.statementCode();

        when(statementsDefinition.findStatementByCode(statementCode))
                .thenReturn(Optional.of(randomOptionalStatement()));
        when(userAgreementRepository.findByUserIdAndStatementCode(request.userId(), statementCode))
                .thenReturn(Optional.of(mock(UserAgreement.class)));

        //when && then
        assertThatThrownBy(() -> userAgreementValidator.validateStatement(request))
                .isInstanceOf(StatementException.class)
                .hasMessage(ExceptionType.USER_AGREEMENT_ALREADY_EXISTS.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        //given
        final UserAgreementRequest request = randomUserAgreementRequest();
        when(userRepository.findById(request.userId())).thenReturn(Optional.empty());

        //when && then
        assertThatThrownBy(() -> userAgreementValidator.validateAndGetUser(request))
                .isInstanceOf(StatementException.class)
                .hasMessage(ExceptionType.USER_NOT_FOUND.getMessage());
    }
}
