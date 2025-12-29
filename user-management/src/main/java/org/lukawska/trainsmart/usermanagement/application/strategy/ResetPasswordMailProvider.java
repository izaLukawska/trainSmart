package org.lukawska.trainsmart.usermanagement.application.strategy;

import org.lukawska.trainsmart.usermanagement.domain.valueObject.TokenType;
import org.springframework.stereotype.Component;

@Component
public class ResetPasswordMailProvider implements MailContentProvider {

    @Override
    public String getSubject() {
        return "Password reset";
    }

    @Override
    public String getBody(String link) {
        return """
                We received reset password request!
                To reset your password click the link below:
                %s
                Best regards,
                Trainsmart Team
                """.formatted(link);
    }

    @Override
    public TokenType getSupportedType() {
        return TokenType.PASSWORD_RESET;
    }
}
