package org.lukawska.trainsmart.usermanagement.application.strategy;

import org.lukawska.trainsmart.usermanagement.domain.valueObject.TokenType;
import org.springframework.stereotype.Component;

@Component
public class AccountActivationMailProvider implements MailContentProvider {

    @Override
    public String getPath() {
        return "/auth/activate";
    }

    @Override
    public String getSubject() {
        return "Account activation";
    }

    @Override
    public String getBody(String link) {
        return """
                Welcome to our sports community!
                To activate your account, please click the link below:
                %s
                Best regards,
                Trainsmart Team
                """.formatted(link);
    }

    @Override
    public TokenType getSupportedType() {
        return TokenType.ACTIVATION;
    }
}
