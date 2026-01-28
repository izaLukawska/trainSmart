package org.lukawska.trainsmart.usermanagement.application.service;

import org.junit.jupiter.api.Test;
import org.lukawska.trainsmart.config.PostgresTestConfig;
import org.lukawska.trainsmart.config.TestFixtures;
import org.lukawska.trainsmart.mailing.application.service.MailService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.usermanagement.application.exception.ExceptionType;
import org.lukawska.trainsmart.usermanagement.application.exception.UserManagementException;
import org.lukawska.trainsmart.usermanagement.domain.entity.VerificationToken;
import org.lukawska.trainsmart.usermanagement.domain.repository.UserRepository;
import org.lukawska.trainsmart.usermanagement.domain.repository.VerificationTokenRepository;
import org.lukawska.trainsmart.usermanagement.model.*;
import org.lukawska.trainsmart.usermanagement.model.RegisterUserRequest.RoleEnum;
import org.lukawska.trainsmart.usermanagement.model.SendVerificationLinkRequest.TokenTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lukawska.trainsmart.testutils.TestData.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Import({PostgresTestConfig.class, TestFixtures.class})
class UserServiceIT {

    @Autowired
    private UserService userService;

    @Autowired
    private TestFixtures testFixtures;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @MockitoBean
    private MailService mailService;

    @Test
    void shouldReturnUserProfileResponseWhenRegisterUser() {
        //given
        final RegisterUserRequest registerUserRequest = new RegisterUserRequest(
                username(), rawPassword(), email(), RoleEnum.ROLE_USER, LocalDate.of(2000, 10, 10));

        //when
        UserProfileResponse result = userService.registerUser(registerUserRequest);

        //then
        assertThat(userRepository.findByUsername(result.getUsername())).isPresent();
        assertThat(result.getUsername()).isEqualTo(registerUserRequest.getUsername());
        assertThat(result.getEmail()).isEqualTo(registerUserRequest.getEmail());
        assertThat(result.getDisabled()).isTrue();
        assertThat(result.getRole().name()).isEqualTo(registerUserRequest.getRole().name());
    }

    @Test
    void shouldReturnUserProfileResponseAndActivateAccount() {
        //given
        final VerificationToken verificationToken = testFixtures.verificationToken().save();
        final String tokenValue = verificationToken.getToken();

        //when
        UserProfileResponse result = userService.activateAccount(tokenValue);

        //then
        assertThat(userRepository.findByUsername(result.getUsername())).isPresent();
        assertThat(result.getDisabled()).isFalse();
    }

    @Test
    @WithMockUser(username = "active_user")
    void shouldChangeEmailWhenValidRequestCredentials() {
        //given
        final User user = testFixtures.user().save();
        final String oldEmail = user.getEmail();
        final String newEmail = email();
        final ChangeEmailRequest changeEmailRequest = new ChangeEmailRequest(oldEmail, newEmail);

        //when
        userService.changeEmail(changeEmailRequest);

        //then
        Optional<User> foundUser = userRepository.findByUsername(user.getUsername());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(newEmail);
    }

    @Test
    @WithMockUser(username = "active_user")
    void shouldSkipUpdateWhenMailUnchanged() {
        //given
        final User user = testFixtures.user().save();
        final String oldEmail = user.getEmail();
        final ChangeEmailRequest changeEmailRequest = new ChangeEmailRequest(oldEmail, oldEmail);

        //when
        userService.changeEmail(changeEmailRequest);

        //then
        Optional<User> foundUser = userRepository.findByUsername(user.getUsername());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getModifiedAt()).isEqualTo(user.getModifiedAt());
    }

    @Test
    @WithMockUser(username = "active_user")
    void shouldChangePasswordWhenValidRequest() {
        //given
        final String previousRawPassword = rawPassword();
        final String newRawPassword = rawPassword();
        final User user = testFixtures.user()
                                      .withPassword(passwordEncoder.encode(previousRawPassword))
                                      .save();
        final ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(
                previousRawPassword, newRawPassword);

        //when
        userService.changePassword(changePasswordRequest);

        //then
        Optional<User> foundUser = userRepository.findByUsername(user.getUsername());
        assertThat(foundUser).isPresent();
        assertThat(passwordEncoder.matches(previousRawPassword, foundUser.get().getPassword())).isFalse();
        assertThat(passwordEncoder.matches(newRawPassword, foundUser.get().getPassword())).isTrue();
    }

    @Test
    @WithMockUser(username = "active_user")
    void shouldSkipUpdateWhenPasswordUnchanged() {
        //given
        final String rawPassword = rawPassword();
        final User user = testFixtures.user()
                                      .withPassword(passwordEncoder.encode(rawPassword))
                                      .save();
        final ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(rawPassword, rawPassword);

        //when
        userService.changePassword(changePasswordRequest);

        //then
        Optional<User> foundUser = userRepository.findByUsername(user.getUsername());
        assertThat(foundUser).isPresent();
        assertThat(passwordEncoder.matches(rawPassword, foundUser.get().getPassword())).isTrue();
        assertThat(foundUser.get().getModifiedAt()).isEqualTo(foundUser.get().getModifiedAt());
    }

    @Test
    void shouldSendVerificationLink() {
        //given
        final User user = testFixtures.user().save();
        final TokenTypeEnum tokenTypeEnum = TokenTypeEnum.ACTIVATION;
        final SendVerificationLinkRequest request = new SendVerificationLinkRequest(user.getUsername(), tokenTypeEnum);

        //when
        userService.sendVerificationLink(request);

        //then
        VerificationToken verificationToken = verificationTokenRepository.findAll().getFirst();
        assertThat(verificationToken.getUser().getId()).isEqualTo(user.getId());
        assertThat(verificationToken.getTokenType().name()).isEqualTo(tokenTypeEnum.name());
        verify(mailService, times(1)).sendMail(any());
    }

    @Test
    void shouldResetPasswordSuccess() {
        //given
        final String oldRawPassword = rawPassword();
        final User user = testFixtures.user()
                                      .withPassword(passwordEncoder.encode(oldRawPassword))
                                      .save();
        final VerificationToken verificationToken = testFixtures.verificationToken()
                                                                .forUser(user)
                                                                .save();
        final String newRawPassword = rawPassword();
        final ResetPasswordRequest request = new ResetPasswordRequest(verificationToken.getToken(), newRawPassword);

        //when
        userService.resetPassword(request);

        //then
        assertThat(verificationTokenRepository.findById(verificationToken.getId())).isNotPresent();
        Optional<User> foundUser = userRepository.findByUsername(user.getUsername());
        assertThat(foundUser).isPresent();
        assertThat(passwordEncoder.matches(newRawPassword, foundUser.get().getPassword())).isTrue();
        assertThat(passwordEncoder.matches(oldRawPassword, foundUser.get().getPassword())).isFalse();
    }

    @Test
    @WithMockUser(username = "active_user")
    void shouldDeleteAccountSuccess() {
        //given
        final User user = testFixtures.user().save();

        //when
        userService.deleteAccount();

        //then
        assertThat(userRepository.findByUsername(user.getUsername())).isNotPresent();
    }

    @Test
    void shouldReturnUserByUsername() {
        //given
        final User user = testFixtures.user().save();

        //when
        User result = userService.getUserByUsername(user.getUsername());

        //then
        assertThat(result.getId()).isEqualTo(user.getId());
    }

    @Test
    @WithMockUser(username = "active_user")
    void shouldThrowUserManagementExceptionWhenInvalidEmail() {
        //given
        testFixtures.user().save();
        final String invalidEmail = email();
        final ChangeEmailRequest changeEmailRequest = new ChangeEmailRequest(invalidEmail, invalidEmail);

        //when
        assertThatThrownBy(() -> userService.changeEmail(changeEmailRequest))
                .isInstanceOf(UserManagementException.class)
                .hasMessage(ExceptionType.INVALID_EMAIL.getMessage());
    }

    @Test
    @WithMockUser(username = "active_user")
    void shouldThrowUserManagementExceptionWhenEmailTaken() {
        //given
        final User user = testFixtures.user().save();
        final User otherUser = testFixtures.user()
                                           .withUsername(username())
                                           .save();
        final ChangeEmailRequest changeEmailRequest = new ChangeEmailRequest(user.getEmail(), otherUser.getEmail());

        //when
        assertThatThrownBy(() -> userService.changeEmail(changeEmailRequest))
                .isInstanceOf(UserManagementException.class)
                .hasMessage(ExceptionType.EMAIL_TAKEN.getMessage());
    }

    @Test
    @WithMockUser(username = "active_user")
    void shouldThrowUserManagementExceptionWhenChangePasswordWithInvalidPassword() {
        //given
        testFixtures.user().save();
        final String wrongPassword = rawPassword();
        final ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(wrongPassword, wrongPassword);

        //when && then
        assertThatThrownBy(() -> userService.changePassword(changePasswordRequest))
                .isInstanceOf(UserManagementException.class)
                .hasMessage(ExceptionType.INVALID_PASSWORD.getMessage());
    }
}
