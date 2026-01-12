package org.lukawska.trainsmart.usermanagement.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.sharedpersistence.application.exception.UserNotFoundException;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.usermanagement.application.exception.ExceptionType;
import org.lukawska.trainsmart.usermanagement.application.exception.UserManagementException;
import org.lukawska.trainsmart.usermanagement.domain.entity.VerificationToken;
import org.lukawska.trainsmart.usermanagement.domain.repository.UserRepository;
import org.lukawska.trainsmart.usermanagement.domain.valueObject.TokenType;
import org.lukawska.trainsmart.usermanagement.model.*;
import org.lukawska.trainsmart.usermanagement.model.RegisterUserRequest.RoleEnum;
import org.lukawska.trainsmart.usermanagement.model.SendVerificationLinkRequest.TokenTypeEnum;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lukawska.trainsmart.usermanagement.application.testutil.UserManagementTestData.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private VerificationTokenService verificationTokenService;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterUserSuccessfully() {
        //given
        final User user = user();
        final RegisterUserRequest registerUserRequest = new RegisterUserRequest(user.getUsername(),
                                                                                user.getPassword(),
                                                                                user.getEmail(),
                                                                                RoleEnum.valueOf(user.getRole().name()),
                                                                                user.getBirthDate());
        when(userRepository.save(any())).thenReturn(user);

        //when
        UserProfileResponse result = userService.registerUser(registerUserRequest);

        //then
        assertThat(result.getUsername()).isEqualTo(user.getUsername());
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        assertThat(result.getRole().name()).isEqualTo(user.getRole().name());
        assertThat(result.getDisabled()).isEqualTo(user.isDisabled());
    }

    @Test
    void activateAccount() {
        //given
        final User user = user();
        final VerificationToken verificationToken = verificationToken(user);
        final String token = verificationToken.getToken();
        when(verificationTokenService.consumeActiveVerificationToken(token)).thenReturn(verificationToken);

        //when
        UserProfileResponse result = userService.activateAccount(token);

        //then
        assertThat(result.getUsername()).isEqualTo(user.getUsername());
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        assertThat(result.getRole().name()).isEqualTo(user.getRole().name());
        assertThat(result.getDisabled()).isEqualTo(user.isDisabled());
    }

    @Test
    void shouldChangeEmail() {
        //given
        final User user = user();
        final String username = user.getUsername();
        final String newEmail = randomEmail();
        final ChangeEmailRequest changeEmailRequest = new ChangeEmailRequest(user.getEmail(), newEmail);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        //when
        userService.changeEmail(username, changeEmailRequest);

        //then
        assertThat(user.getEmail()).isEqualTo(newEmail);
    }

    @Test
    void shouldChangePassword() {
        //given
        final User user = user();
        final String username = user.getUsername();
        final String newPassword = randomString();
        final ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(user.getPassword(), newPassword);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(user.getPassword(), changePasswordRequest.getPreviousPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(newPassword);

        //when
        userService.changePassword(username, changePasswordRequest);

        //then
        assertThat(user.getPassword()).isEqualTo(newPassword);
    }

    @Test
    void shouldSendVerificationLink() {
        //given
        final User user = user();
        final TokenType tokenType = TokenType.PASSWORD_RESET;
        final SendVerificationLinkRequest request = new SendVerificationLinkRequest(
                user.getUsername(), TokenTypeEnum.valueOf(tokenType.name()));
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        //when
        userService.sendVerificationLink(request);

        //then
        verify(verificationTokenService).sendVerificationMail(user, tokenType);
    }

    @Test
    void shouldResetPassword() {
        //given
        final User user = user();
        final VerificationToken verificationToken = verificationToken(user);
        final String token = verificationToken.getToken();
        final String expectedPassword = randomString();
        final ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(token, expectedPassword);

        when(verificationTokenService.consumeActiveVerificationToken(token)).thenReturn(verificationToken);
        when(passwordEncoder.encode(expectedPassword)).thenReturn(expectedPassword);

        //when
        userService.resetPassword(resetPasswordRequest);

        //then
        verify(refreshTokenService).deleteRefreshTokenForUser(user.getUsername());
        assertThat(user.getPassword()).isEqualTo(expectedPassword);
    }

    @Test
    void shouldDeleteAccount() {
        //given
        final String username = randomString();

        //when
        userService.deleteAccount(username);

        //then
        verify(userRepository).deleteByUsername(username);
    }

    @Test
    void shouldReturnUserByUsername() {
        //given
        final User user = user();
        final String username = user.getUsername();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        //when
        User result = userService.getUserByUsername(username);

        //then
        assertThat(result.getUsername()).isEqualTo(user.getUsername());
        assertThat(result.getPassword()).isEqualTo(user.getPassword());
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        assertThat(result.getRole()).isEqualTo(user.getRole());
        assertThat(result.getBirthDate()).isEqualTo(user.getBirthDate());
    }

    @Test
    void shouldThrowInvalidEmailExceptionWhenChangeEmail() {
        //given
        final User user = user();
        final ChangeEmailRequest changeEmailRequest = new ChangeEmailRequest(randomEmail(), randomEmail());
        final String username = user.getUsername();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        //when && then
        assertThatThrownBy(() -> userService.changeEmail(username, changeEmailRequest))
                .isInstanceOf(UserManagementException.class)
                .hasMessage(ExceptionType.INVALID_EMAIL.getMessage());
    }

    @Test
    void shouldThrowEmailTakenExceptionWhenChangeEmail() {
        //given
        final User user = user();
        final String username = user.getUsername();
        final ChangeEmailRequest changeEmailRequest = new ChangeEmailRequest(user.getEmail(), randomEmail());
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenThrow(new DataIntegrityViolationException("violation"));

        //when && then
        assertThatThrownBy(() -> userService.changeEmail(username, changeEmailRequest))
                .isInstanceOf(UserManagementException.class)
                .hasMessage(ExceptionType.EMAIL_TAKEN.getMessage());
    }

    @Test
    void shouldThrowInvalidPasswordExceptionWhenChangePassword() {
        //given
        final User user = user();
        final String username = user.getUsername();
        final String password = user.getPassword();
        final ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(password, randomString());

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, changePasswordRequest.getPreviousPassword())).thenReturn(false);

        //when && then
        assertThatThrownBy(() -> userService.changePassword(username, changePasswordRequest))
                .isInstanceOf(UserManagementException.class)
                .hasMessage(ExceptionType.INVALID_PASSWORD.getMessage());
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenGetUserByUsername() {
        //given
        final String username = randomString();
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        //when && then
        assertThatThrownBy(() -> userService.getUserByUsername(username)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void shouldThrowUserAlreadyExistsExceptionWhenRegisterUser() {
        //given
        final RegisterUserRequest registerUserRequest = new RegisterUserRequest(randomString(),
                                                                                randomString(),
                                                                                randomString(),
                                                                                RoleEnum.ROLE_USER,
                                                                                LocalDate.of(2000, 10, 10));
        when(userRepository.save(any())).thenThrow(new DataIntegrityViolationException("violation"));

        //when && then
        assertThatThrownBy(() -> userService.registerUser(registerUserRequest))
                .isInstanceOf(UserManagementException.class)
                .hasMessage(ExceptionType.USER_ALREADY_EXISTS.getMessage());
    }
}
