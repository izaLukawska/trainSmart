package org.lukawska.trainsmart.usermanagement.application.dto.user.response;

import org.lukawska.trainsmart.sharedpersistence.domain.valueObjects.Role;
import org.lukawska.trainsmart.sharedpersistence.domain.valueObjects.Status;

import java.time.LocalDate;

public record UserProfileResponse(String username, String email, Role role, LocalDate birthDate, Status status) {}
