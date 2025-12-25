package org.lukawska.trainsmart.usermanagement.application.resolvers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lukawska.trainsmart.usermanagement.application.exception.ExceptionType;
import org.lukawska.trainsmart.usermanagement.application.exception.UserException;
import org.lukawska.trainsmart.usermanagement.application.strategy.MailContentProvider;
import org.lukawska.trainsmart.usermanagement.domain.valueObject.TokenType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailContentProviderResolver {

    private final List<MailContentProvider> contentProviders;

    public MailContentProvider getProvider(TokenType tokenType) {
        return contentProviders.stream()
                               .filter(provider -> provider.getSupportedType() == tokenType)
                               .findFirst()
                               .map(provider -> {
                                   log.info("Mail content provider {} found for type: {}",
                                            provider.getClass().getSimpleName(), tokenType);
                                   return provider;
                               })
                               .orElseThrow(() -> new UserException(ExceptionType.MAIL_PROVIDER_NOT_FOUND));
    }
}
