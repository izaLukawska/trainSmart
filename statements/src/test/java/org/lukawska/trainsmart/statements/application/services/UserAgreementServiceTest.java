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
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lukawska.trainsmart.statements.testutil.StatementTestData.*;
import static org.lukawska.trainsmart.statements.testutil.UserAgreementTestData.*;
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
    void shouldCreateNewUserAgreementWhenSignAgreement() {
        //given
        final UserAgreementRequest request = acceptedUserAgreementRequest();
        final UserAgreement savedUserAgreement = acceptedUserAgreement(request.statementCode(), 1);
        
        when(userAgreementValidator.validateUserAgreement(request)).thenReturn(requiredStatement());
        when(userAgreementRepository.findByUserIdAndStatementCode(1L, request.statementCode()))
                .thenReturn(Optional.empty());
        when(userAgreementRepository.save(any())).thenReturn(savedUserAgreement);

        //when
        UserAgreementResponse result = userAgreementService.signAgreement(1L, request);

        //then
        assertThat(result.id()).isEqualTo(savedUserAgreement.getId());
        assertThat(result.statementCode()).isEqualTo(savedUserAgreement.getStatementCode());
        assertThat(result.agreementStatus()).isEqualTo(savedUserAgreement.getStatus());
    }

    @Test
    void shouldUpdateUserAgreementWhenSignAgreement() {
        //given
        final UserAgreementRequest request = rejectedUserAgreementRequest(randomStatementCode());
        final UserAgreement existingUserAgreement = acceptedUserAgreement(request.statementCode(), 1);

        when(userAgreementValidator.validateUserAgreement(any())).thenReturn(optionalStatement());
        when(userAgreementRepository.findByUserIdAndStatementCode(1L, request.statementCode()))
                .thenReturn(Optional.of(existingUserAgreement));
        when(userAgreementRepository.save(any())).thenReturn(existingUserAgreement);

        //when
        UserAgreementResponse result = userAgreementService.signAgreement(1L, request);

        //then
        assertThat(result.id()).isEqualTo(existingUserAgreement.getId());
        assertThat(result.agreementStatus()).isEqualTo(request.agreementStatus());
        assertThat(result.statementCode()).isEqualTo(request.statementCode());
    }

    @Test
    void shouldReturnRequiredStatementsToSign() {
        //given
        final Long userId = new Random().nextLong(10);
        final List<UserAgreement> userAgreements = List.of(acceptedUserAgreement(randomStatementCode(), 1),
                                                           acceptedUserAgreement(randomStatementCode(), 1));

        when(userService.getUserById(userId)).thenReturn(mock(User.class));
        when(userAgreementRepository.findAllByUserId(userId)).thenReturn(userAgreements);
        when(userAgreementValidator.outdatedUserAgreement(any())).thenReturn(true);

        //when
        List<UserAgreementResponse> result = userAgreementService.getRequiredStatementsToSign(userId);

        //then
        assertThat(result).hasSize(2);
        assertThat(result.getFirst().statementCode()).isEqualTo(userAgreements.getFirst().getStatementCode());
        assertThat(result.getLast().statementCode()).isEqualTo(userAgreements.getLast().getStatementCode());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenSignAgreement() {
        //given
        final UserAgreementRequest request = acceptedUserAgreementRequest();
        when(userService.getUserById(1L)).thenThrow(new UserNotFoundException(1L));

        //when && then
        assertThatThrownBy(() -> userAgreementService.signAgreement(1L, request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found for ID: %d", 1);
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
