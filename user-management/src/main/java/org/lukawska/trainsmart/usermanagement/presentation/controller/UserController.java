package org.lukawska.trainsmart.usermanagement.presentation.controller;

import lombok.RequiredArgsConstructor;
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
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserProfileResponse> register(@RequestBody RegisterUserRequest registerUserRequest) {
        return ResponseEntity.status(201).body(userService.register(registerUserRequest));
    }
}
