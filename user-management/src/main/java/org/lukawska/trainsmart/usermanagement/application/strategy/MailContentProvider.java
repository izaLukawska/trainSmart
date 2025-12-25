package org.lukawska.trainsmart.usermanagement.application.strategy;

import org.lukawska.trainsmart.mailing.application.dto.MailRequest;
import org.lukawska.trainsmart.usermanagement.domain.valueObject.TokenType;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

public interface MailContentProvider {

    String getPath();

    String getSubject();

    String getBody(String link);

    TokenType getSupportedType();

    default String generateLink(String baseUrl, String token) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                                   .path(getPath())
                                   .queryParam("token", token)
                                   .toUriString();
    }

    default MailRequest createMailRequest(String email, String baseUrl, String token) {
        String link = generateLink(baseUrl, token);
        return new MailRequest(List.of(email),
                               Collections.emptyList(),
                               Collections.emptyList(),
                               getSubject(),
                               getBody(link),
                               false,
                               Collections.emptyList());
    }
}
