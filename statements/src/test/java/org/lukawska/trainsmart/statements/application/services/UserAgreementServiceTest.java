package org.lukawska.trainsmart.statements.application.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.shared_persistence.application.exception.UserException;
import org.lukawska.trainsmart.shared_persistence.application.service.UserService;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementRequest;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementResponse;
import org.lukawska.trainsmart.statements.application.exception.ExceptionType;
import org.lukawska.trainsmart.statements.application.exception.StatementException;
import org.lukawska.trainsmart.statements.application.validation.UserAgreementValidator;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.lukawska.trainsmart.statements.domain.repositories.UserAgreementRepository;
import org.lukawska.trainsmart.statements.domain.valueObjects.AgreementStatus;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lukawska.trainsmart.shared_persistence.application.exception.ExceptionType.USER_NOT_FOUND;
import static org.lukawska.trainsmart.statements.testutil.StatementTestData.requiredStatement;
import static org.lukawska.trainsmart.statements.testutil.UserAgreementTestData.acceptedUserAgreement;
import static org.lukawska.trainsmart.statements.testutil.UserAgreementTestData.acceptedUserAgreementRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAgreementServiceTest {

    @Mock
    private UserAgreementRepository userAgreementRepository;

    @Mock
    private UserAgreementValidator userAgreementValidator;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserAgreementService userAgreementService;

    @Test
    void shouldSignNewAgreementSuccess() {
        //given
        final UserAgreementRequest request = acceptedUserAgreementRequest();
        when(userAgreementValidator.validateNewUserAgreement(request)).thenReturn(requiredStatement());
        when(userAgreementRepository.save(any())).thenReturn(new UserAgreement(mock(User.class),
                                                                               request.statementCode(),
                                                                               1,
                                                                               AgreementStatus.ACCEPTED));

        //when
        UserAgreementResponse result = userAgreementService.signNewAgreement(request);

        //then
        assertThat(result.statementCode()).isEqualTo(request.statementCode());
        assertThat(result.agreementStatus()).isEqualTo(request.agreementStatus());
    }

    @Test
    void shouldReSignStatementSuccess() {
        final UserAgreementRequest request = acceptedUserAgreementRequest();
        when(userAgreementValidator.validateUserAgreement(any())).thenReturn(requiredStatement(2));
        when(userAgreementRepository.findByUserIdAndStatementCode(request.userId(), request.statementCode()))
                .thenReturn(Optional.of(new UserAgreement(mock(User.class),
                                                          "statementCode",
                                                          1,
                                                          AgreementStatus.ACCEPTED)));

        // when
        UserAgreementResponse result = userAgreementService.reSignAgreement(request);

        // then
        assertThat(result.statementCode()).isEqualTo("statementCode");
        assertThat(result.agreementStatus()).isEqualTo(AgreementStatus.ACCEPTED);
        assertThat(result.version()).isEqualTo(2);
    }

    @Test
    void shouldReturnRequiredStatementsToSign() {
        //given
        final Long userId = new Random().nextLong(10);
        List<UserAgreement> userAgreements = List.of(acceptedUserAgreement(), acceptedUserAgreement());

        when(userAgreementRepository.findAllByUserId(userId)).thenReturn(userAgreements);
        when(userAgreementValidator.outdatedUserAgreement(any())).thenReturn(true);

        //when
        List<UserAgreementResponse> result = userAgreementService.getRequiredStatementsToSign(userId);

        //then
        verify(userService).getUserById(userId);
        assertThat(result).hasSize(2);
        assertThat(result.getFirst().statementCode()).isEqualTo(userAgreements.getFirst().getStatementCode());
        assertThat(result.getLast().statementCode()).isEqualTo(userAgreements.getLast().getStatementCode());

    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenSignNewAgreement() {
        //given
        final UserAgreementRequest request = acceptedUserAgreementRequest();
        when(userService.getUserById(request.userId())).thenThrow(new UserException(USER_NOT_FOUND));

        //when && then
        assertThatThrownBy(() -> userAgreementService.signNewAgreement(request))
                .isInstanceOf(UserException.class)
                .hasMessage(USER_NOT_FOUND.getMessage());
        verify(userAgreementRepository, never()).save(any());
    }

    @Test
    void shouldThrowUserAgreementNotFoundExceptionWhenReSignAgreement() {
        //given
        final UserAgreementRequest request = acceptedUserAgreementRequest();
        when(userAgreementValidator.validateUserAgreement(request)).thenReturn(requiredStatement());

        //when && then
        assertThatThrownBy(() -> userAgreementService.reSignAgreement(request))
                .isInstanceOf(StatementException.class)
                .hasMessage(ExceptionType.USER_AGREEMENT_NOT_FOUND.getMessage());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenGetRequiredStatementsToSign() {
        //given
        when(userService.getUserById(1L)).thenThrow(new UserException(USER_NOT_FOUND));

        //when && then
        assertThatThrownBy(() -> userAgreementService.getRequiredStatementsToSign(1L))
                .isInstanceOf(UserException.class)
                .hasMessage(USER_NOT_FOUND.getMessage());
    }
}
