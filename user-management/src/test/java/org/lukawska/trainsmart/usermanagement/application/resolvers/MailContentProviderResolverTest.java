package org.lukawska.trainsmart.usermanagement.application.resolvers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lukawska.trainsmart.usermanagement.application.exception.ExceptionType;
import org.lukawska.trainsmart.usermanagement.application.exception.UserManagementException;
import org.lukawska.trainsmart.usermanagement.application.strategy.MailContentProvider;
import org.lukawska.trainsmart.usermanagement.domain.valueObject.TokenType;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailContentProviderResolverTest {

    @Test
    void shouldReturnProvider() {
        // given
        final TokenType tokenType = TokenType.ACTIVATION;
        final MailContentProvider matchingProvider = mock(MailContentProvider.class);
        final MailContentProvider notMatchingProvider = mock(MailContentProvider.class);
        final List<MailContentProvider> providers = List.of(matchingProvider, notMatchingProvider);
        final MailContentProviderResolver mailContentProviderResolver = new MailContentProviderResolver(providers);
        when(matchingProvider.getSupportedType()).thenReturn(tokenType);

        //when
        MailContentProvider result = mailContentProviderResolver.getProvider(tokenType);

        //then
        assertThat(result).isEqualTo(matchingProvider);
    }

    @Test
    void shouldThrowMailProviderNotFoundExceptionWhenGetProvider() {
        //given
        final TokenType tokenType = TokenType.PASSWORD_RESET;
        final MailContentProvider notMatchingProvider1 = mock(MailContentProvider.class);
        final MailContentProvider notMatchingProvider2 = mock(MailContentProvider.class);
        final List<MailContentProvider> providers = List.of(notMatchingProvider1, notMatchingProvider2);
        final MailContentProviderResolver mailContentProviderResolver = new MailContentProviderResolver(providers);

        //when && then
        assertThatThrownBy(() -> mailContentProviderResolver.getProvider(tokenType))
                .isInstanceOf(UserManagementException.class)
                .hasMessage(ExceptionType.MAIL_PROVIDER_NOT_FOUND.getMessage());
    }
}
