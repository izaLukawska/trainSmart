package org.lukawska.trainsmart.statements.application.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.sharedpersistence.application.exception.UserNotFoundException;
import org.lukawska.trainsmart.sharedpersistence.application.service.UserAccessService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.statements.application.validation.UserAgreementValidator;
import org.lukawska.trainsmart.statements.domain.entities.UserAgreement;
import org.lukawska.trainsmart.statements.domain.repositories.UserAgreementRepository;
import org.lukawska.trainsmart.statements.model.UserAgreementRequest;
import org.lukawska.trainsmart.statements.model.UserAgreementResponse;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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
    private UserAccessService userService;

    private static final Long USER_ID = 1L;
    @InjectMocks
    private UserAgreementService userAgreementService;

    @Test
    void shouldCreateNewUserAgreementWhenSignAgreement() {
        //given
        final UserAgreementRequest request = acceptedUserAgreementRequest();
        final UserAgreement savedUserAgreement = acceptedUserAgreement(request.getStatementCode(), 1);

        when(userAgreementValidator.validateUserAgreement(any())).thenReturn(requiredStatement());
        when(userAgreementRepository.findByUserIdAndStatementCode(USER_ID, request.getStatementCode()))
                .thenReturn(Optional.empty());
        when(userAgreementRepository.save(any())).thenReturn(savedUserAgreement);

        //when
        UserAgreementResponse result = userAgreementService.signAgreement(USER_ID, request);

        //then
        assertThat(result.getId()).isEqualTo(savedUserAgreement.getId());
        assertThat(result.getStatementCode()).isEqualTo(savedUserAgreement.getStatementCode());
        assertThat(result.getAgreementStatus().name()).isEqualTo(savedUserAgreement.getStatus().name());
    }

    @Test
    void shouldUpdateUserAgreementWhenSignAgreement() {
        //given
        final UserAgreementRequest request = rejectedUserAgreementRequest(randomStatementCode());
        final UserAgreement existingUserAgreement = acceptedUserAgreement(request.getStatementCode(), 1);

        when(userAgreementValidator.validateUserAgreement(any())).thenReturn(optionalStatement());
        when(userAgreementRepository.findByUserIdAndStatementCode(USER_ID, request.getStatementCode()))
                .thenReturn(Optional.of(existingUserAgreement));
        when(userAgreementRepository.save(any())).thenReturn(existingUserAgreement);

        //when
        UserAgreementResponse result = userAgreementService.signAgreement(USER_ID, request);

        //then
        assertThat(result.getId()).isEqualTo(existingUserAgreement.getId());
        assertThat(result.getAgreementStatus().name()).isEqualTo(request.getAgreementStatus().name());
        assertThat(result.getStatementCode()).isEqualTo(request.getStatementCode());
    }

    @Test
    void shouldReturnRequiredStatementsToSign() {
        //given
        final List<UserAgreement> userAgreements = List.of(acceptedUserAgreement(randomStatementCode(), 1),
                                                           acceptedUserAgreement(randomStatementCode(), 1));

        when(userService.getUserById(USER_ID)).thenReturn(mock(User.class));
        when(userAgreementRepository.findAllByUserId(USER_ID)).thenReturn(userAgreements);
        when(userAgreementValidator.outdatedUserAgreement(any())).thenReturn(true);

        //when
        List<UserAgreementResponse> result = userAgreementService.getRequiredStatementsToSign(USER_ID);

        //then
        assertThat(result).hasSize(userAgreements.size());
        assertThat(result.getFirst().getStatementCode()).isEqualTo(userAgreements.getFirst().getStatementCode());
        assertThat(result.getLast().getStatementCode()).isEqualTo(userAgreements.getLast().getStatementCode());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenSignAgreement() {
        //given
        final UserAgreementRequest request = acceptedUserAgreementRequest();
        when(userService.getUserById(USER_ID)).thenThrow(new UserNotFoundException());

        //when && then
        assertThatThrownBy(() -> userAgreementService.signAgreement(USER_ID, request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");
        verify(userAgreementRepository, never()).save(any());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenGetRequiredStatementsToSign() {
        //given
        when(userService.getUserById(USER_ID)).thenThrow(new UserNotFoundException());

        //when && then
        assertThatThrownBy(() -> userAgreementService.getRequiredStatementsToSign(USER_ID))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");
    }
}
