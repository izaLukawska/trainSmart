package org.lukawska.trainsmart.usermanagement.application.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.sharedpersistence.application.service.UserAccessService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.sharedpersistence.domain.repositories.UserRepository;
import org.lukawska.trainsmart.usermanagement.application.dto.user.request.EmailUpdateRequest;
import org.lukawska.trainsmart.usermanagement.application.dto.user.request.PasswordResetRequest;
import org.lukawska.trainsmart.usermanagement.application.dto.user.request.PasswordUpdateRequest;
import org.lukawska.trainsmart.usermanagement.application.exception.ExceptionType;
import org.lukawska.trainsmart.usermanagement.application.exception.UserException;
import org.lukawska.trainsmart.usermanagement.application.service.registration.VerificationTokenService;
import org.lukawska.trainsmart.usermanagement.domain.repository.RefreshTokenRepository;
import org.lukawska.trainsmart.usermanagement.domain.valueObject.TokenType;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final UserAccessService userAccessService;

    private final PasswordEncoder passwordEncoder;

    private final RefreshTokenRepository refreshTokenRepository;

    private final VerificationTokenService verificationTokenService;

    @Transactional
    public void changeEmail(Long userId, EmailUpdateRequest emailUpdateRequest) {
        log.info("Changing mail for user {}", userId);
        User user = userAccessService.getUserById(userId);
        if (!user.getEmail().equals(emailUpdateRequest.oldEmail())) {
            throw new UserException(ExceptionType.INVALID_EMAIL);
        }

        try {
            user.changeEmail(emailUpdateRequest.newMail());
            userRepository.save(user);
            log.info("Mail updated");
        } catch (DataIntegrityViolationException e) {
            throw new UserException(ExceptionType.EMAIL_TAKEN);
        }
    }

    @Transactional
    public void changePassword(Long userId, PasswordUpdateRequest changePasswordRequest) {
        log.info("Changing password for user {}", userId);
        User user = userAccessService.getUserById(userId);

        if (!passwordEncoder.matches(changePasswordRequest.currentPassword(), user.getPassword())) {
            throw new UserException(ExceptionType.INVALID_PASSWORD);
        }

        user.changePassword(changePasswordRequest.newPassword(), passwordEncoder);
        userRepository.save(user);
        log.info("Password changed");

        refreshTokenRepository.deleteAllByUserId(userId);
    }

    @Transactional
    public void resetPassword(Long userId, PasswordResetRequest passwordResetRequest) {
        log.info("Password reset for user {}", userId);
        User user = userAccessService.getUserById(userId);

        verificationTokenService.sendVerificationMail(user, TokenType.PASSWORD_RESET);

    }
}
