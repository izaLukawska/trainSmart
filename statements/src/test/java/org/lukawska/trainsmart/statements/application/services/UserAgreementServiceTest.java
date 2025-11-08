package org.lukawska.trainsmart.statements.application.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.shared_persistence.application.exception.UserNotFoundException;
import org.lukawska.trainsmart.shared_persistence.application.service.UserService;
import org.lukawska.trainsmart.shared_persistence.domain.entities.User;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementRequest;
import org.lukawska.trainsmart.statements.application.dto.UserAgreementResponse;
import org.lukawska.trainsmart.statements.application.validation.UserAgreementValidator;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.lukawska.trainsmart.statements.domain.repositories.UserAgreementRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lukawska.trainsmart.statements.testutil.StatementTestData.randomStatementCode;
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
    void shouldSignAgreementSuccess() {
        //given
        final UserAgreementRequest request = acceptedUserAgreementRequest();
        when(userAgreementValidator.validateUserAgreement(request)).thenReturn(requiredStatement());
        when(userAgreementRepository.save(any())).thenReturn(acceptedUserAgreement(request.statementCode(), 1));

        //when
        UserAgreementResponse result = userAgreementService.signAgreement(request);

        //then
        assertThat(result.statementCode()).isEqualTo(request.statementCode());
        assertThat(result.agreementStatus()).isEqualTo(request.agreementStatus());
    }

    @Test
    void shouldReturnRequiredStatementsToSign() {
        //given
        final Long userId = new Random().nextLong(10);
        List<UserAgreement> userAgreements = List.of(acceptedUserAgreement(randomStatementCode(), 1),
                                                     acceptedUserAgreement(randomStatementCode(), 1));

        when(userService.getUserById(userId)).thenReturn(mock(User.class));
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
    void shouldThrowUserNotFoundExceptionWhenSignAgreement() {
        //given
        final UserAgreementRequest request = acceptedUserAgreementRequest();
        when(userService.getUserById(request.userId())).thenThrow(new UserNotFoundException(request.userId()));

        //when && then
        assertThatThrownBy(() -> userAgreementService.signAgreement(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found for ID: %d", request.userId());
        verify(userAgreementRepository, never()).save(any());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenGetRequiredStatementsToSign() {
        //given
        when(userService.getUserById(1L)).thenThrow(new UserNotFoundException(1L));

        //when && then
        assertThatThrownBy(() -> userAgreementService.getRequiredStatementsToSign(1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found for ID: %d", 1L);
    }
}
