package org.lukawska.trainsmart.statements.application.services;

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

    @Test
    void shouldSignStatementSuccess() {
        //given
        final int version = new Random().nextInt();
        final Statement statement = new Statement(UUID.randomUUID().toString(),
                                                  version,
                                                  false,
                                                  UUID.randomUUID().toString());
        final Long userId = new Random().nextLong(10);
        final String code = UUID.randomUUID().toString();
        final AgreementStatus status = AgreementStatus.ACCEPTED;
        final Long id = new Random().nextLong(10);
        when(definitions.findStatementByCode(code)).thenReturn(Optional.of(statement));
        when(agreementRepository.findByUserIdAndStatementCode(userId, code)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);

        UserAgreement saved = mock(UserAgreement.class);
        when(saved.getId()).thenReturn(id);
        when(saved.getUser()).thenReturn(user);
        when(saved.getStatementCode()).thenReturn(code);
        when(saved.getStatementVersion()).thenReturn(version);
        when(saved.getStatus()).thenReturn(status);

        when(agreementRepository.save(any())).thenReturn(saved);

        final UserAgreementRequest request = new UserAgreementRequest(userId, code, status);
        final UserAgreementResponse expected = new UserAgreementResponse(id, userId, code, version, status);

        //when
        UserAgreementResponse result = service.signStatement(request);

        //then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldResignStatementSuccessfully() {
        //given
        final Statement statement = new Statement(UUID.randomUUID().toString(),
                                                  2,
                                                  false,
                                                  UUID.randomUUID().toString());
        final String code = UUID.randomUUID().toString();
        final Long userId = new Random().nextLong(10);
        final AgreementStatus status = AgreementStatus.ACCEPTED;
        when(definitions.findStatementByCode(code)).thenReturn(Optional.of(statement));
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        UserAgreement existing = new UserAgreement(user, code, 1, status);
        when(agreementRepository.findByUserIdAndStatementCode(userId, code)).thenReturn(Optional.of(existing));
        final UserAgreementRequest request = new UserAgreementRequest(userId, code, status);
        UserAgreementResponse expected = new UserAgreementResponse(null, userId, code, 2, status);
        //when
        UserAgreementResponse result = service.resignStatement(request);

        //then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldReturnEmptyListWhenRequiredUserAgreementToSignNotFound() {
        //given
        final Long userId = new Random().nextLong();
        final String statementCode = UUID.randomUUID().toString();
        final AgreementStatus status = AgreementStatus.ACCEPTED;
        final User user = mock(User.class);
        final UserAgreement userAgreement = new UserAgreement(user, statementCode, 2, status);
        when(agreementRepository.findAllByUserId(userId)).thenReturn(List.of(userAgreement));
        when(definitions.getRequiredStatementsMap()).thenReturn(Map.of());

        //when && then
        assertThat(service.getRequiredStatementsToSign(userId)).isEmpty();
    }

    @Test
    void shouldReturnRequiredUserAgreementToSignWhenFound() {
        //given
        final Long userId = new Random().nextLong(5);
        final String statementCode = UUID.randomUUID().toString();
        final AgreementStatus status = AgreementStatus.ACCEPTED;
        final User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        final UserAgreement userAgreement = new UserAgreement(user, statementCode, 1, status);
        final Statement required = new Statement(statementCode, 2, true, UUID.randomUUID().toString());
        final UserAgreementResponse expectedUa = new UserAgreementResponse(null,
                                                                           userId,
                                                                           statementCode,
                                                                           1,
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
        final Statement statement = new Statement(UUID.randomUUID().toString(),
                                                  new Random().nextInt(),
                                                  false,
                                                  UUID.randomUUID().toString());
        final Long userId = new Random().nextLong(100);
        final String code = UUID.randomUUID().toString();
        when(definitions.findStatementByCode(code)).thenReturn(Optional.of(statement));
        when(agreementRepository.findByUserIdAndStatementCode(userId, code))
                .thenReturn(Optional.of(mock(UserAgreement.class)));
        final UserAgreementRequest request = new UserAgreementRequest(userId, code, AgreementStatus.REJECTED);

        //when && then
        assertThatThrownBy(() -> service.signStatement(request))
                .isInstanceOf(StatementException.class)
                .hasMessage(ExceptionType.USER_AGREEMENT_ALREADY_EXISTS.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenStatementNotFound() {
        //given
        final String statementCode = UUID.randomUUID().toString();
        final Long userId = new Random().nextLong();
        final UserAgreementRequest request = new UserAgreementRequest(userId, statementCode, AgreementStatus.ACCEPTED);
        when(definitions.findStatementByCode(statementCode)).thenReturn(Optional.empty());

        //when && then
        assertThatThrownBy(() -> service.signStatement(request))
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
        assertThatThrownBy(() -> service.signStatement(request))
                .isInstanceOf(StatementException.class)
                .hasMessage(ExceptionType.STATEMENT_ACCEPTANCE_REQUIRED.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        //given
        final String statementCode = UUID.randomUUID().toString();
        final Statement statement = new Statement(UUID.randomUUID().toString(),
                                                  new Random().nextInt(),
                                                  false,
                                                  UUID.randomUUID().toString());
        when(definitions.findStatementByCode(statementCode)).thenReturn(Optional.of(statement));
        final UserAgreementRequest request = new UserAgreementRequest(new Random().nextLong(), statementCode,
                                                                      AgreementStatus.REJECTED);

        //when && then
        assertThatThrownBy(() -> service.signStatement(request))
                .isInstanceOf(StatementException.class)
                .hasMessage(ExceptionType.USER_NOT_FOUND.getMessage());
    }
}
