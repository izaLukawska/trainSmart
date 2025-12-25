package org.lukawska.trainsmart.usermanagement.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.usermanagement.application.dto.user.request.RegisterUserRequest;
import org.lukawska.trainsmart.usermanagement.application.dto.user.response.UserProfileResponse;
import org.springframework.security.crypto.password.PasswordEncoder;

@UtilityClass
public class UserMapper {

    public static User mapToUser(RegisterUserRequest req, PasswordEncoder passwordEncoder) {
        return User.builder()
                   .username(req.username())
                   .password(passwordEncoder.encode(req.password()))
                   .email(req.email())
                   .role(req.role())
                   .birthDate(req.birthDate())
                   .build();
    }

    public static UserProfileResponse mapToProfileResponse(User user) {
        return new UserProfileResponse(
                user.getUsername(), user.getEmail(), user.getRole(), user.getBirthDate(), user.getStatus());
    }
}
