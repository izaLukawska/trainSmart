package org.lukawska.trainsmart.commons.jwt;

public interface JwtService {

    String generateAccessToken(String username);

    String extractUsername(String token);

    boolean validToken(String token);

}
