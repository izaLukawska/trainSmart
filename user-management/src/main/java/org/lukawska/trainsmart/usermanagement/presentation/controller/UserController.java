package org.lukawska.trainsmart.usermanagement.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.usermanagement.application.dto.request.user.*;
import org.lukawska.trainsmart.usermanagement.application.dto.response.UserProfileResponse;
import org.lukawska.trainsmart.usermanagement.application.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/me/change-password")
    public ResponseEntity<Void> changePassword(Authentication auth, @RequestBody @Valid ChangePasswordRequest request) {
        String username = auth.getName();
        log.info("Change password request received for user {}", username);
        userService.changePassword(username, request);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/me/change-email")
    public ResponseEntity<Void> changeEmail(Authentication auth, @RequestBody @Valid ChangeEmailRequest request) {
        String username = auth.getName();
        log.info("Change email request received for user {}", username);
        userService.changeEmail(username, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<UserProfileResponse> register(@RequestBody @Valid RegisterUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(request));
    }

    @PostMapping("/activate")
    public ResponseEntity<UserProfileResponse> activate(@RequestParam String token) {
        return ResponseEntity.ok(userService.activateAccount(token));
    }

    @PostMapping("/send-verification-link")
    public ResponseEntity<Void> sendVerificationLink(@RequestBody @Valid SendVerificationLinkRequest request) {
        userService.sendVerificationLink(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/password-reset")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.ok().build();
    }
}
