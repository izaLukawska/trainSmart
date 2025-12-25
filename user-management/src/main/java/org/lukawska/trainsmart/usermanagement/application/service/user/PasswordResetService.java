package org.lukawska.trainsmart.usermanagement.application.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.sharedpersistence.application.service.UserAccessService;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.usermanagement.application.dto.user.request.PasswordResetRequest;
import org.lukawska.trainsmart.usermanagement.application.service.registration.VerificationTokenService;
import org.lukawska.trainsmart.usermanagement.domain.entity.VerificationToken;
import org.lukawska.trainsmart.usermanagement.domain.repository.RefreshTokenRepository;
import org.lukawska.trainsmart.usermanagement.domain.valueObject.TokenType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class PasswordResetService {

    private final UserAccessService userAccessService;

    private final PasswordEncoder passwordEncoder;

    private final RefreshTokenRepository refreshTokenRepository;

    private final VerificationTokenService verificationTokenService;

    @Transactional
    public void resetPassword(Long userId) {
        log.info("Password reset for user {}", userId);
        User user = userAccessService.getUserById(userId);
        verificationTokenService.sendVerificationMail(user, TokenType.PASSWORD_RESET);
    }

    @Transactional
    public void finalizePasswordReset(PasswordResetRequest passwordResetRequest) {
        VerificationToken verificationToken = verificationTokenService.consumeActiveVerificationToken(
                passwordResetRequest.token());
        User user = verificationToken.getUser();
        user.changePassword(passwordResetRequest.newPassword(), passwordEncoder);

        refreshTokenRepository.deleteAllByUserId(user.getId());
    }
}
