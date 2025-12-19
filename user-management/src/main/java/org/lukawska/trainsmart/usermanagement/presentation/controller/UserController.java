package org.lukawska.trainsmart.usermanagement.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.usermanagement.application.dto.user.request.RegisterUserRequest;
import org.lukawska.trainsmart.usermanagement.application.dto.user.response.UserProfileResponse;
import org.lukawska.trainsmart.usermanagement.application.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserProfileResponse> register(@RequestBody RegisterUserRequest registerUserRequest) {
        log.info("Registering user with username: {}", registerUserRequest.username());
        return ResponseEntity.status(201).body(userService.register(registerUserRequest));
    }
}
