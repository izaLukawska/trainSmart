package org.lukawska.trainsmart.security.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.lukawska.trainsmart.commons.jwt.JwtService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;

    private final SecretKey secretKey;

    public JwtServiceImpl(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret()));
    }

    @Override
    public String generateAccessToken(String username) {
        return generateToken(username, jwtProperties.getAccessExpMs());
    }

    @Override
    public String generateRefreshToken(String username) {
        return generateToken(username, jwtProperties.getRefreshExpMs());
    }

    @Override
    public String extractUsername(String token) {
        return Jwts.parser()
                   .verifyWith(secretKey)
                   .build()
                   .parseSignedClaims(token)
                   .getPayload()
                   .getSubject();
    }

    @Override
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    private String generateToken(String name, long expiration) {
        Instant currDate = Instant.now();

        return Jwts.builder()
                   .subject(name)
                   .issuedAt(Date.from(currDate))
                   .expiration(Date.from(currDate.plusMillis(expiration)))
                   .signWith(secretKey)
                   .compact();
    }
}
