package org.lukawska.trainsmart.usermanagement.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.usermanagement.application.dto.request.user.RegisterUserRequest;
import org.lukawska.trainsmart.usermanagement.application.dto.response.UserProfileResponse;

@UtilityClass
public class UserMapper {

    public static User mapToUser(RegisterUserRequest request, String encodedPassword) {
        return User.builder()
                   .username(request.username())
                   .password(encodedPassword)
                   .email(request.email())
                   .role(request.role())
                   .birthDate(request.birthDate())
                   .build();
    }

    public static UserProfileResponse mapToProfileResponse(User user) {
        return new UserProfileResponse(user.getUsername(), user.getEmail(), user.getRole(), user.isDisabled());
    }
}
