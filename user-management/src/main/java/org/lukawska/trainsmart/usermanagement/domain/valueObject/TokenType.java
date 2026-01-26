package org.lukawska.trainsmart.usermanagement.domain.valueObject;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public enum TokenType {

    PASSWORD_RESET, ACTIVATION

}
