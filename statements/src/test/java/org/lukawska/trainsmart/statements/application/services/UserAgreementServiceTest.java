package org.lukawska.trainsmart.statements.application.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementRequest;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementResponse;
import org.lukawska.trainsmart.statements.application.exception.ExceptionType;
import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.lukawska.trainsmart.statements.application.validation.UserAgreementValidator;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.lukawska.trainsmart.statements.domain.repositories.UserAgreementRepository;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;
import org.lukawska.trainsmart.statements.infra.config.Statement;
import org.lukawska.trainsmart.statements.infra.config.StatementsDefinition;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lukawska.trainsmart.statements.testutil.StatementTestData.*;
import static org.lukawska.trainsmart.statements.testutil.UserAgreementTestData.randomUserAgreementRequest;
import static org.lukawska.trainsmart.statements.testutil.UserAgreementTestData.specificUserAgreement;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAgreementServiceTest {

    @Mock
    private UserAgreementRepository agreementRepository;

    @Mock
    private StatementsDefinition statementsDefinition;

    @Mock
    private UserAgreementValidator userAgreementValidator;

    @InjectMocks
    private UserAgreementService userAgreementService;

    @Test
    void shouldSignNewAgreementSuccess() {
        //given
        final UserAgreementRequest request = randomUserAgreementRequest();
        when(userAgreementValidator.validateStatement(request)).thenReturn(randomRequiredStatement());
        User mockedUser = mock(User.class);
        when(userAgreementValidator.validateAndGetUser(request)).thenReturn(mockedUser);
        when(agreementRepository.save(any()))
                .thenReturn(specificUserAgreement(mockedUser, request.statementCode(), 1));

        //when
        UserAgreementResponse result = userAgreementService.signNewAgreement(request);

        //then
        verify(agreementRepository).save(any());
        assertThat(result).extracting(UserAgreementResponse::statementCode,
                                      UserAgreementResponse::version)
                          .contains(result.statementCode(), 1);

    }

    @Test
    void shouldReSignStatementSuccess() {
        final Long userId = new Random().nextLong();
        final String statementCode = randomStatementCode();
        final UserAgreementRequest request = new UserAgreementRequest(userId,
                                                                      statementCode,
                                                                      AgreementStatus.ACCEPTED);

        when(userAgreementValidator.validateStatement(request)).thenReturn(statementWithVersion(2));
        final User mockedUser = mock(User.class);
        when(mockedUser.getId()).thenReturn(userId);
        when(agreementRepository.findByUserIdAndStatementCode(userId, statementCode))
                .thenReturn(Optional.of(specificUserAgreement(mockedUser, statementCode, 1)));

        // when
        UserAgreementResponse result = userAgreementService.reSignAgreement(request);

        // then
        assertThat(result).extracting(UserAgreementResponse::userId,
                                      UserAgreementResponse::statementCode,
                                      UserAgreementResponse::version)
                          .contains(userId, statementCode, 2);
    }

    @Test
    void shouldReturnRequiredStatementsToSign() {
        //given
        final Long userId = new Random().nextLong(10);
        final String statementCode = randomStatementCode();
        List<UserAgreement> userAgreements = List.of(specificUserAgreement(mock(User.class), statementCode, 1),
                                                     specificUserAgreement(mock(User.class), statementCode, 2));
        when(agreementRepository.findAllByUserId(userId)).thenReturn(userAgreements);
        when(statementsDefinition.getRequiredStatementsMap())
                .thenReturn(Map.of(statementCode, statementWithVersion(2)));

        //when
        List<UserAgreementResponse> result = userAgreementService.getRequiredStatementsToSign(userId);

        //then
        assertThat(result).hasSize(1)
                          .extracting(UserAgreementResponse::statementCode).containsOnly(statementCode);
    }

    @Test
    void shouldThrowUserAgreementAlreadyExistsExceptionWhenSignNewAgreement() {
        //given
        final Statement statement = randomRequiredStatement();
        final UserAgreementRequest request = randomUserAgreementRequest();
        when(userAgreementValidator.validateStatement(request)).thenReturn(statement);
        when(agreementRepository.findByUserIdAndStatementCode(request.userId(), request.statementCode()))
                .thenReturn(Optional.of(mock(UserAgreement.class)));

        //when && then
        assertThatThrownBy(() -> userAgreementService.signNewAgreement(request))
                .isInstanceOf(StatementException.class)
                .hasMessage(ExceptionType.USER_AGREEMENT_ALREADY_EXISTS.getMessage());
    }

    @Test
    void shouldThrowUserAgreementNotFoundExceptionWhenReSignAgreement() {
        //given
        final Statement statement = randomRequiredStatement();
        final UserAgreementRequest request = randomUserAgreementRequest();
        when(userAgreementValidator.validateStatement(request)).thenReturn(statement);

        //when && then
        assertThatThrownBy(() -> userAgreementService.reSignAgreement(request))
                .isInstanceOf(StatementException.class)
                .hasMessage(ExceptionType.USER_AGREEMENT_NOT_FOUND.getMessage());
    }
}
