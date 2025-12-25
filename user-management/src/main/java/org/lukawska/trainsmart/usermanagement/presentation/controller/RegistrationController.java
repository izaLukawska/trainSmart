package org.lukawska.trainsmart.usermanagement.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.usermanagement.application.dto.user.request.RegisterUserRequest;
import org.lukawska.trainsmart.usermanagement.application.dto.user.response.UserProfileResponse;
import org.lukawska.trainsmart.usermanagement.application.service.registration.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
@Slf4j
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<UserProfileResponse> register(@RequestBody RegisterUserRequest registerUserRequest) {
        log.info("Register request received");
        return ResponseEntity.status(201).body(registrationService.register(registerUserRequest));
    }

    @GetMapping("/activate")
    public ResponseEntity<UserProfileResponse> activate(@RequestParam String token) {
        log.info("Activate account request received.");
        return ResponseEntity.ok(registrationService.activateAccount(token));
    }
}
