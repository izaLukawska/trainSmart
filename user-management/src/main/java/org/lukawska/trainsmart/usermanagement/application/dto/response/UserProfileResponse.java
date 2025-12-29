package org.lukawska.trainsmart.usermanagement.application.dto.response;

import org.lukawska.trainsmart.sharedpersistence.domain.valueObjects.Role;

public record UserProfileResponse(String username, String email, Role role, boolean disabled) {}
