package org.lukawska.trainsmart.sharedpersistence.domain.valueObjects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public enum Role implements GrantedAuthority {

    ROLE_USER, ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
