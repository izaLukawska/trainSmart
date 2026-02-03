package org.lukawska.trainsmart.usermanagement.application.testutil;

import lombok.experimental.UtilityClass;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.sharedpersistence.domain.valueObjects.Role;
import org.lukawska.trainsmart.usermanagement.domain.entity.RefreshToken;
import org.lukawska.trainsmart.usermanagement.domain.entity.VerificationToken;
import org.lukawska.trainsmart.usermanagement.domain.valueObject.TokenType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@UtilityClass
public class UserManagementTestData {

    public static User user() {
        return User.builder()
                   .username(randomString())
                   .password(randomString())
                   .email(randomEmail())
                   .role(Role.ROLE_USER)
                   .birthDate(LocalDate.of(1990, 10, 10))
                   .build();
    }

    public static RefreshToken userRefreshToken(User user) {
        return new RefreshToken(randomString(), user, Instant.MAX, false);
    }

    public static VerificationToken verificationToken(User user) {
        return new VerificationToken(randomString(), user, Instant.MAX, TokenType.ACTIVATION);
    }

    public static String randomString() {
        return UUID.randomUUID().toString();
    }

    public static String randomEmail() {
        return randomString().concat("@example.com");
    }
}
