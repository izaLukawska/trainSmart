package org.lukawska.trainsmart.usermanagement.presentation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.usermanagement.application.dto.user.request.EmailUpdateRequest;
import org.lukawska.trainsmart.usermanagement.application.dto.user.request.PasswordUpdateRequest;
import org.lukawska.trainsmart.usermanagement.application.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/users/{userId}")
@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@PathVariable @Positive Long userId,
                                               @RequestBody @Valid PasswordUpdateRequest changePasswordRequest) {
        log.info("Change password request received for user {}", userId);
        userService.changePassword(userId, changePasswordRequest);

        return ResponseEntity.noContent().build();

    }

    @PutMapping("/change-email")
    public ResponseEntity<Void> changeEmail(@PathVariable @Positive Long userId,
                                            @RequestBody @Valid EmailUpdateRequest emailUpdateRequest) {
        log.info("Change email request received for user {}", userId);
        userService.changeEmail(userId, emailUpdateRequest);

        return ResponseEntity.noContent().build();

    }
}
