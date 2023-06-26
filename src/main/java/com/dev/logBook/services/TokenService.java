package com.dev.logBook.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.dev.logBook.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
public class TokenService {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${token.expiration}")
    private long tokenExpiration;

    @Value("${timezone.offset}")
    private String timezoneOffset;

    public String generateToken(User user) {
        return JWT.create()
                .withIssuer("JWT")
                .withSubject(user.getUsername())
                .withClaim("id", user.getId().toString())
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(Date.from(LocalDateTime.now()
                        .plusSeconds(tokenExpiration)
                        .toInstant(ZoneOffset.of(timezoneOffset)))
                ).sign(Algorithm.HMAC256(jwtSecret));
    }

    public String getSubject(String token) {
        return JWT.require(Algorithm.HMAC256(jwtSecret))
                .withIssuer("JWT")
                .build().verify(token).getSubject();
    }

}
