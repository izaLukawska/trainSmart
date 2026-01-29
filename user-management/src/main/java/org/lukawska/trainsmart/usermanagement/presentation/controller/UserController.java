package org.lukawska.trainsmart.usermanagement.presentation.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.usermanagement.api.UserApi;
import org.lukawska.trainsmart.usermanagement.application.service.UserService;
import org.lukawska.trainsmart.usermanagement.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    @SecurityRequirements
    public ResponseEntity<UserProfileResponse> activateAccount(String token) {
        log.info("Received activate account request");

        return ResponseEntity.ok(userService.activateAccount(token));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Override
    public ResponseEntity<Void> changeEmail(ChangeEmailRequest changeEmailRequest) {
        log.info("Received change email request");
        userService.changeEmail(changeEmailRequest);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Override
    public ResponseEntity<Void> changePassword(ChangePasswordRequest changePasswordRequest) {
        log.info("Received change password request");
        userService.changePassword(changePasswordRequest);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Override
    public ResponseEntity<Void> deleteAccount() {
        log.info("Received delete account request ");
        userService.deleteAccount();

        return ResponseEntity.noContent().build();
    }

    @Override
    @SecurityRequirements
    public ResponseEntity<UserProfileResponse> register(RegisterUserRequest registerUserRequest) {
        log.info("Received registration request for user {}", registerUserRequest.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(registerUserRequest));
    }

    @Override
    @SecurityRequirements
    public ResponseEntity<Void> resetPassword(ResetPasswordRequest resetPasswordRequest) {
        log.info("Received reset password request");
        userService.resetPassword(resetPasswordRequest);

        return ResponseEntity.ok().build();
    }

    @Override
    @SecurityRequirements
    public ResponseEntity<Void> sendVerificationLink(SendVerificationLinkRequest sendVerificationLinkRequest) {
        log.info("Received send verification link request for type: {}",
                 sendVerificationLinkRequest.getTokenType().getValue());
        userService.sendVerificationLink(sendVerificationLinkRequest);

        return ResponseEntity.accepted().build();
    }
}
