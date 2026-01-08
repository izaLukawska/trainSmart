package org.lukawska.trainsmart.usermanagement.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.usermanagement.api.UsersApi;
import org.lukawska.trainsmart.usermanagement.application.service.UserService;
import org.lukawska.trainsmart.usermanagement.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController implements UsersApi {

    private final UserService userService;

    @Override
    public ResponseEntity<UserProfileResponse> activateAccount(String token) {
        log.info("Received activate account request");

        return ResponseEntity.ok(userService.activateAccount(token));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Override
    public ResponseEntity<Void> changeEmail(ChangeEmailRequest changeEmailRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Change email request received for user {}", username);
        userService.changeEmail(username, changeEmailRequest);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Override
    public ResponseEntity<Void> changePassword(ChangePasswordRequest changePasswordRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Change password request received for user {}", username);
        userService.changePassword(username, changePasswordRequest);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Override
    public ResponseEntity<Void> deleteAccount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Delete account request received for user {}", username);
        userService.deleteAccount(username);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<UserProfileResponse> register(RegisterUserRequest registerUserRequest) {
        log.info("Received registration request for user {}", registerUserRequest.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(registerUserRequest));
    }

    @Override
    public ResponseEntity<Void> resetPassword(ResetPasswordRequest resetPasswordRequest) {
        log.info("Received reset password request");
        userService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> sendVerificationLink(SendVerificationLinkRequest sendVerificationLinkRequest) {
        log.info("Received send verification link request for type: {}",
                 sendVerificationLinkRequest.getTokenType().getValue());
        userService.sendVerificationLink(sendVerificationLinkRequest);

        return ResponseEntity.accepted().build();
    }
}
