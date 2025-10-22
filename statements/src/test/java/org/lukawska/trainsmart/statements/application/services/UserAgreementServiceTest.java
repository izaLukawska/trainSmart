package org.lukawska.trainsmart.statements.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;
import org.lukawska.trainsmart.shared_persistence.domain.repositories.UserRepository;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementRequest;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementResponse;
import org.lukawska.trainsmart.statements.application.exception.ExceptionType;
import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.lukawska.trainsmart.statements.domain.repositories.UserAgreementRepository;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;
import org.lukawska.trainsmart.statements.infra.config.Statement;
import org.lukawska.trainsmart.statements.infra.config.StatementsDefinition;
import org.lukawska.trainsmart.statements.testutil.StatementTestData;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAgreementServiceTest {

    @Mock
    private UserAgreementRepository agreementRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StatementsDefinition definitions;

    @InjectMocks
    private UserAgreementService service;

    private String statementCode;

    private Long userId;

    private UserAgreementRequest baseRequest;


    @BeforeEach
    void setUp() {
        statementCode = UUID.randomUUID().toString();
        userId = new Random().nextLong(10);
        baseRequest = new UserAgreementRequest(userId, statementCode, AgreementStatus.ACCEPTED);
    }

    @Test
    void shouldSignStatementSuccess() {
        //given
        final User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        final Statement statement = StatementTestData.randomStatement();
        when(definitions.findStatementByCode(statementCode)).thenReturn(Optional.of(statement));
        when(agreementRepository.findByUserIdAndStatementCode(userId, statementCode)).thenReturn(Optional.empty());

        final AgreementStatus status = AgreementStatus.ACCEPTED;
        UserAgreement userAgreement = new UserAgreement(user,
                                                        statementCode,
                                                        statement.version(),
                                                        status);

        when(agreementRepository.save(any())).thenReturn(userAgreement);
        final UserAgreementRequest request = baseRequest;
        final UserAgreementResponse expected = new UserAgreementResponse(null,
                                                                         userId,
                                                                         statementCode,
                                                                         statement.version(),
                                                                         status);

        //when
        UserAgreementResponse result = service.signStatement(request);

        //then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldResignStatementSuccessfully() {
        //given
        final Statement statement = StatementTestData.randomStatement();
        when(definitions.findStatementByCode(statementCode)).thenReturn(Optional.of(statement));

        final User user = mock(User.class);
        when(user.getId()).thenReturn(userId);

        final AgreementStatus status = AgreementStatus.ACCEPTED;
        final UserAgreementResponse expected = new UserAgreementResponse(null,
                                                                         userId,
                                                                         statementCode,
                                                                         statement.version(),
                                                                         status);

        UserAgreement userAgreement = new UserAgreement(user,
                                                        statementCode,
                                                        statement.version() - 1,
                                                        status);

        when(agreementRepository.findByUserIdAndStatementCode(userId, statementCode))
                .thenReturn(Optional.of(userAgreement));

        UserAgreementRequest request = baseRequest;

        //when
        UserAgreementResponse result = service.resignStatement(request);

        //then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldReturnEmptyListWhenRequiredUserAgreementToSignNotFound() {
        //given;
        final AgreementStatus status = AgreementStatus.ACCEPTED;
        final Statement statement = StatementTestData.statement(true);
        final UserAgreement userAgreement = new UserAgreement(mock(User.class),
                                                              statementCode,
                                                              statement.version(),
                                                              status);

        when(agreementRepository.findAllByUserId(userId)).thenReturn(List.of(userAgreement));
        when(definitions.getRequiredStatementsMap()).thenReturn(Map.of(statementCode, statement));

        //when && then
        assertThat(service.getRequiredStatementsToSign(userId)).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenAllRequiredUserAgreementValid() {
        //given
        final AgreementStatus status = AgreementStatus.ACCEPTED;
        final UserAgreement userAgreement = new UserAgreement(mock(User.class),
                                                              statementCode,
                                                              new Random().nextInt(),
                                                              status);

        when(agreementRepository.findAllByUserId(userId)).thenReturn(List.of(userAgreement));
        when(definitions.getRequiredStatementsMap()).thenReturn(Map.of());

        //when && then
        assertThat(service.getRequiredStatementsToSign(userId)).isEmpty();
    }

    @Test
    void shouldReturnRequiredUserAgreementToSignWhenFound() {
        //given
        final User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        final Statement required = StatementTestData.statement(true);
        final AgreementStatus status = AgreementStatus.ACCEPTED;

        final UserAgreement userAgreement = new UserAgreement(user, statementCode, required.version() - 1, status);
        final UserAgreementResponse expectedUa = new UserAgreementResponse(null,
                                                                           userId,
                                                                           statementCode,
                                                                           required.version() - 1,
                                                                           status);

        when(agreementRepository.findAllByUserId(userId)).thenReturn(List.of(userAgreement));
        when(definitions.getRequiredStatementsMap()).thenReturn(Map.of(statementCode, required));

        //when && then
        assertThat(service.getRequiredStatementsToSign(userId))
                .hasSize(1)
                .contains(expectedUa);
    }

    @Test
    void shouldThrowExceptionWhenUserAgreementExists() {
        //given
        final UserAgreementRequest request = new UserAgreementRequest(userId, statementCode, AgreementStatus.ACCEPTED);
        when(definitions.findStatementByCode(statementCode))
                .thenReturn(Optional.of(StatementTestData.randomStatement()));
        when(agreementRepository.findByUserIdAndStatementCode(userId, statementCode))
                .thenReturn(Optional.of(mock(UserAgreement.class)));

        //when && then
        assertThatThrownBy(() -> service.signStatement(request))
                .isInstanceOf(StatementException.class)
                .hasMessage(ExceptionType.USER_AGREEMENT_ALREADY_EXISTS.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenStatementRequiredButRejected() {
        //given
        final UserAgreementRequest request = new UserAgreementRequest(userId, statementCode, AgreementStatus.REJECTED);
        when(definitions.findStatementByCode(statementCode))
                .thenReturn(Optional.of(StatementTestData.statement(true)));

        //when && then
        assertThatThrownBy(() -> service.signStatement(request))
                .isInstanceOf(StatementException.class)
                .hasMessage(ExceptionType.STATEMENT_ACCEPTANCE_REQUIRED.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenStatementNotFound() {
        //given
        final UserAgreementRequest request = new UserAgreementRequest(userId, statementCode, AgreementStatus.ACCEPTED);
        when(definitions.findStatementByCode(statementCode)).thenReturn(Optional.empty());

        //when && then
        assertThatThrownBy(() -> service.signStatement(request))
                .isInstanceOf(StatementException.class)
                .hasMessage(ExceptionType.STATEMENT_NOT_FOUND.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        //given
        final UserAgreementRequest request = new UserAgreementRequest(userId, statementCode, AgreementStatus.ACCEPTED);
        when(definitions.findStatementByCode(statementCode))
                .thenReturn(Optional.of(StatementTestData.randomStatement()));

        //when && then
        assertThatThrownBy(() -> service.signStatement(request))
                .isInstanceOf(StatementException.class)
                .hasMessage(ExceptionType.USER_NOT_FOUND.getMessage());
    }
}
