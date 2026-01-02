package org.lukawska.trainsmart.usermanagement.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.sharedpersistence.application.exception.UserNotFoundException;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.usermanagement.application.dto.request.user.*;
import org.lukawska.trainsmart.usermanagement.application.dto.response.UserProfileResponse;
import org.lukawska.trainsmart.usermanagement.application.exception.ExceptionType;
import org.lukawska.trainsmart.usermanagement.application.exception.UserManagementException;
import org.lukawska.trainsmart.usermanagement.domain.entity.VerificationToken;
import org.lukawska.trainsmart.usermanagement.domain.repository.UserRepository;
import org.lukawska.trainsmart.usermanagement.domain.valueObject.TokenType;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.lukawska.trainsmart.usermanagement.application.mapper.UserMapper.mapToProfileResponse;
import static org.lukawska.trainsmart.usermanagement.application.mapper.UserMapper.mapToUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RefreshTokenService refreshTokenService;

    private final VerificationTokenService verificationTokenService;

    @Transactional
    public UserProfileResponse registerUser(RegisterUserRequest registerUserRequest) {
        log.info("Registering user with username: {}", registerUserRequest.username());
        try {
            String encodedPassword = passwordEncoder.encode(registerUserRequest.password());
            User newUser = mapToUser(registerUserRequest, encodedPassword);
            userRepository.save(newUser);
            log.info("Saved user with ID {}", newUser.getId());
            return mapToProfileResponse(newUser);
        } catch (DataIntegrityViolationException e) {
            throw new UserManagementException(ExceptionType.USER_ALREADY_EXISTS);
        }
    }

    @Transactional
    public UserProfileResponse activateAccount(String token) {
        VerificationToken activationToken = verificationTokenService.consumeActiveVerificationToken(token);
        User user = activationToken.getUser();

        log.info("Activating account for user {}", user.getId());
        user.activate();
        userRepository.save(user);
        log.info("Account activated");

        return mapToProfileResponse(user);
    }

    @Transactional
    public void changeEmail(String username, ChangeEmailRequest emailUpdateRequest) {
        log.info("Changing mail for user {}", username);
        User user = getUserByUsername(username);
        if (!user.getEmail().equals(emailUpdateRequest.oldEmail())) {
            throw new UserManagementException(ExceptionType.INVALID_EMAIL);
        }

        try {
            user.changeEmail(emailUpdateRequest.newMail());
            userRepository.save(user);
            log.info("Mail updated");
        } catch (DataIntegrityViolationException e) {
            throw new UserManagementException(ExceptionType.EMAIL_TAKEN);
        }
    }

    @Transactional
    public void changePassword(String username, ChangePasswordRequest changePasswordRequest) {
        log.info("Changing password for user {}", username);
        User user = getUserByUsername(username);

        if (!passwordEncoder.matches(changePasswordRequest.oldPassword(), user.getPassword())) {
            throw new UserManagementException(ExceptionType.INVALID_PASSWORD);
        }

        updatePassword(user, changePasswordRequest.newPassword());
    }

    @Transactional
    public void sendVerificationLink(SendVerificationLinkRequest request) {
        User user = getUserByUsername(request.username());
        TokenType tokenType = request.tokenType();
        log.info("Sending {} mail for user {}", tokenType.name(), user.getUsername());
        verificationTokenService.sendVerificationMail(user, tokenType);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        VerificationToken verificationToken = verificationTokenService.consumeActiveVerificationToken(
                resetPasswordRequest.token());

        User user = verificationToken.getUser();
        log.info("Resetting password for user {}", user.getId());
        updatePassword(user, resetPasswordRequest.newPassword());
    }

    @Transactional
    public void deleteAccount(String username) {
        userRepository.deleteByUsername(username);
        log.info("Account deleted for user: {}", username);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
    }

    private void updatePassword(User user, String newPassword) {
        log.info("Updating password for user {}", user.getId());
        user.changePassword(newPassword, passwordEncoder);
        refreshTokenService.deleteRefreshTokenForUser(user.getUsername());
        userRepository.save(user);
        log.info("Password updated");
    }
}
