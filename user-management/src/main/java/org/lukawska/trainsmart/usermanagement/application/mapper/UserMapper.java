package org.lukawska.trainsmart.usermanagement.application.mapper;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.sharedpersistence.domain.valueObjects.Role;
import org.lukawska.trainsmart.usermanagement.model.RegisterUserRequest;
import org.lukawska.trainsmart.usermanagement.model.UserProfileResponse;

@UtilityClass
public class UserMapper {

    public static User mapToUser(RegisterUserRequest request, String encodedPassword) {
        return User.builder()
                   .username(request.getUsername())
                   .password(encodedPassword)
                   .email(request.getEmail())
                   .role(Role.valueOf(request.getRole().name()))
                   .birthDate(request.getBirthDate())
                   .build();
    }

    public static UserProfileResponse mapToProfileResponse(User user) {
        return new UserProfileResponse(user.getUsername(), user.getEmail(),
                                       UserProfileResponse.RoleEnum.valueOf(user.getRole().name()),
                                       user.isDisabled());
    }
}
