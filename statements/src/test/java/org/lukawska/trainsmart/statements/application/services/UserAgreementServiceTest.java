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

    private AgreementStatus baseStatus;

    private User user;

    @BeforeEach
    void setUp() {
        statementCode = UUID.randomUUID().toString();
        userId = new Random().nextLong(10);
        baseRequest = new UserAgreementRequest(userId, statementCode, AgreementStatus.ACCEPTED);
        baseStatus = AgreementStatus.ACCEPTED;
        user = mock(User.class);
    }

    private UserAgreement userAgreement(int version) {
        return new UserAgreement(user, statementCode, version, baseStatus);
    }

    @Test
    void shouldSignStatementSuccess() {
        //given
        when(user.getId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        final Statement statement = StatementTestData.randomStatement();
        when(definitions.findStatementByCode(statementCode)).thenReturn(Optional.of(statement));
        when(agreementRepository.findByUserIdAndStatementCode(userId, statementCode)).thenReturn(Optional.empty());

        UserAgreement userAgreement = userAgreement(statement.version());

        when(agreementRepository.save(any())).thenReturn(userAgreement);

        //when
        UserAgreementResponse result = service.signNewAgreement(baseRequest);

        //then
        assertThat(result).extracting(UserAgreementResponse::userId,
                                      UserAgreementResponse::statementCode,
                                      UserAgreementResponse::version)
                          .containsExactly(userId, statementCode, statement.version());
    }

    @Test
    void shouldResignStatementSuccessfully() {
        //given
        final Statement statement = StatementTestData.randomStatement();
        when(definitions.findStatementByCode(statementCode)).thenReturn(Optional.of(statement));
        when(user.getId()).thenReturn(userId);
        UserAgreement userAgreement = userAgreement(statement.version() - 1);

        when(agreementRepository.findByUserIdAndStatementCode(userId, statementCode))
                .thenReturn(Optional.of(userAgreement));

        //when
        UserAgreementResponse result = service.reSignAgreement(baseRequest);

        //then
        assertThat(result).extracting(UserAgreementResponse::userId,
                                      UserAgreementResponse::statementCode,
                                      UserAgreementResponse::version)
                          .containsExactly(userId, statementCode, statement.version());
    }

    @Test
    void shouldReturnEmptyListWhenRequiredUserAgreementToSignNotFound() {
        //given;
        final Statement statement = StatementTestData.statement(true);
        final UserAgreement userAgreement = userAgreement(statement.version());

        when(agreementRepository.findAllByUserId(userId)).thenReturn(List.of(userAgreement));
        when(definitions.getRequiredStatementsMap()).thenReturn(Map.of(statementCode, statement));

        //when && then
        assertThat(service.getRequiredStatementsToSign(userId)).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenAllRequiredUserAgreementValid() {
        //given
        final UserAgreement userAgreement = userAgreement(new Random().nextInt(20));

        when(agreementRepository.findAllByUserId(userId)).thenReturn(List.of(userAgreement));
        when(definitions.getRequiredStatementsMap()).thenReturn(Map.of());

        //when && then
        assertThat(service.getRequiredStatementsToSign(userId)).isEmpty();
    }

    @Test
    void shouldReturnRequiredUserAgreementsToSignWhenFound() {
        //given
        final Statement required = StatementTestData.statement(true);
        final int currVersion = required.version() - 1;
        final UserAgreement userAgreement = userAgreement(currVersion);

        when(user.getId()).thenReturn(userId);
        when(agreementRepository.findAllByUserId(userId)).thenReturn(List.of(userAgreement));
        when(definitions.getRequiredStatementsMap()).thenReturn(Map.of(statementCode, required));

        //when
        List<UserAgreementResponse> result = service.getRequiredStatementsToSign(userId);

        //then
        assertThat(result).singleElement()
                          .extracting(UserAgreementResponse::userId,
                                      UserAgreementResponse::statementCode,
                                      UserAgreementResponse::version)
                          .containsExactly(userId, statementCode, currVersion);
    }

    @Test
    void shouldThrowExceptionWhenUserAgreementExists() {
        //given
        when(definitions.findStatementByCode(statementCode))
                .thenReturn(Optional.of(StatementTestData.randomStatement()));
        when(agreementRepository.findByUserIdAndStatementCode(userId, statementCode))
                .thenReturn(Optional.of(mock(UserAgreement.class)));

        //when && then
        assertThatThrownBy(() -> service.signNewAgreement(baseRequest))
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
        assertThatThrownBy(() -> service.signNewAgreement(request))
                .isInstanceOf(StatementException.class)
                .hasMessage(ExceptionType.STATEMENT_ACCEPTANCE_REQUIRED.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenStatementNotFound() {
        //when && then
        assertThatThrownBy(() -> service.signNewAgreement(baseRequest))
                .isInstanceOf(StatementException.class)
                .hasMessage(ExceptionType.STATEMENT_NOT_FOUND.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        //given
        when(definitions.findStatementByCode(statementCode))
                .thenReturn(Optional.of(StatementTestData.randomStatement()));

        //when && then
        assertThatThrownBy(() -> service.signNewAgreement(baseRequest))
                .isInstanceOf(StatementException.class)
                .hasMessage(ExceptionType.USER_NOT_FOUND.getMessage());
    }
}
