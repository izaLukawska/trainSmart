package org.lukawska.trainsmart.usermanagement.application.dto.user.response;

import org.lukawska.trainsmart.sharedpersistence.domain.valueObjects.Role;

import java.time.LocalDate;

public record UserProfileResponse(String username, String email, Role role, LocalDate birthDate) {}
