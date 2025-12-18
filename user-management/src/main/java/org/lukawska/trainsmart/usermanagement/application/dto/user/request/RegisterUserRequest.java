package org.lukawska.trainsmart.usermanagement.application.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.lukawska.trainsmart.sharedpersistence.domain.valueObjects.Role;

import java.time.LocalDate;

public record RegisterUserRequest(@NotBlank String username,
                                  @NotBlank String password,
                                  @Email String email,
                                  @NotNull Role role,
                                  @NotNull LocalDate birthDate) {}
