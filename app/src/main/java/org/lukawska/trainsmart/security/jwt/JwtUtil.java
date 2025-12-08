package org.lukawska.trainsmart.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;

    private final SecretKey secretKey;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret()));
    }

    public String validateToken(String token) {
        return Jwts.parser()
                   .verifyWith(secretKey)
                   .build()
                   .parseSignedClaims(token)
                   .getPayload()
                   .getSubject();
    }

    public String generateAccessToken(String name) {
        Instant currDate = Instant.now();

        return Jwts.builder()
                   .subject(name)
                   .issuedAt(Date.from(currDate))
                   .expiration(Date.from(currDate.plusMillis(jwtProperties.getAccessExp())))
                   .signWith(secretKey)
                   .compact();
    }

    public String generateRefreshToken(String name) {
        Instant currDate = Instant.now();

        return Jwts.builder()
                   .subject(name)
                   .issuedAt(Date.from(currDate))
                   .expiration(Date.from(currDate.plusMillis(jwtProperties.getRefreshExp())))
                   .signWith(secretKey)
                   .compact();
    }
}
