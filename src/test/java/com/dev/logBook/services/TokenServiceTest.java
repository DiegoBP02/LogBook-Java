package com.dev.logBook.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.dev.logBook.ApplicationConfigTest;
import com.dev.logBook.entities.User;
import com.dev.logBook.entities.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest extends ApplicationConfigTest {
    @Autowired
    TokenService tokenService;
    User USER_RECORD = new User("username", "email", "password", Role.ROLE_USER);
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${token.expiration}")
    private long tokenExpiration;
    @Value("${timezone.offset}")
    private String timezoneOffset;

    @BeforeEach
    void setupSecurityContext() {
        ReflectionTestUtils.setField(USER_RECORD, "id", UUID.randomUUID());
    }

    @Test
    @DisplayName("should generate a token")
    void generateToken_NotNull() {
        String token = tokenService.generateToken(USER_RECORD);

        assertNotNull(token);
    }

    @Test
    @DisplayName("should generate a token with correct issuer")
    void generateToken_CorrectIssuer() {
        String token = tokenService.generateToken(USER_RECORD);

        DecodedJWT decodedToken = JWT.decode(token);

        assertEquals("JWT", decodedToken.getIssuer());
    }

    @Test
    @DisplayName("should generate a token with correct claim")
    void generateToken_CorrectClaims() {
        String token = tokenService.generateToken(USER_RECORD);

        DecodedJWT decodedToken = JWT.decode(token);

        assertEquals(USER_RECORD.getId().toString(),
                decodedToken.getClaim("id").asString());
    }

    @Test
    @DisplayName("should generate a token with correct issued time")
    void generateToken_CorrectIssuedAt() {
        String token = tokenService.generateToken(USER_RECORD);

        DecodedJWT decodedToken = JWT.decode(token);
        Date issuedAt = decodedToken.getIssuedAt();

        // Potential timing variations in test execution
        long toleranceMilliseconds = 2000;
        long issuedTime = issuedAt.getTime();
        long currentTime = System.currentTimeMillis();

        assertThat(issuedTime).isCloseTo(currentTime, within(toleranceMilliseconds));
    }

    @Test
    @DisplayName("should generate a token with correct expiration")
    void generateToken_CorrectExpiration() {
        String token = tokenService.generateToken(USER_RECORD);

        DecodedJWT decodedToken = JWT.decode(token);
        Date expiration = decodedToken.getExpiresAt();
        Date expectedExpiration = Date.from(LocalDateTime.now().plusDays(1)
                .toInstant(ZoneOffset.of("-03:00")));

        // Potential timing variations in test execution
        long toleranceMilliseconds = 2000;
        long expirationTime = expiration.getTime();
        long expectedExpirationTime = expectedExpiration.getTime();

        assertThat(expirationTime).isCloseTo(expectedExpirationTime,
                within(toleranceMilliseconds));
    }

    @Test
    @DisplayName("should generate a token with correct signature")
    void generateToken_CorrectSignature() {
        String token = tokenService.generateToken(USER_RECORD);

        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        verifier.verify(token);
    }

    @Test
    @DisplayName("should throw SignatureVerificationException if signature is incorrect")
    void generateToken_IncorrectSignature() {
        String token = tokenService.generateToken(USER_RECORD);

        Algorithm algorithm = Algorithm.HMAC256("invalidSecret");
        JWTVerifier verifier = JWT.require(algorithm).build();
        assertThrows(SignatureVerificationException.class, () -> {
            verifier.verify(token);
        });
    }

    @Test
    @DisplayName("should generate a token with all valid values")
    void generateToken_CorrectToken() {
        String token = tokenService.generateToken(USER_RECORD);
        DecodedJWT decodedToken = JWT.decode(token);

        assertEquals("JWT", decodedToken.getIssuer());
        assertEquals(USER_RECORD.getId().toString(),
                decodedToken.getClaim("id").asString());

        Date issuedAt = decodedToken.getIssuedAt();
        // Potential timing variations in test execution
        long toleranceMilliseconds = 2000;
        long issuedTime = issuedAt.getTime();
        long currentTime = System.currentTimeMillis();
        assertThat(issuedTime).isCloseTo(currentTime, within(toleranceMilliseconds));

        Date expiration = decodedToken.getExpiresAt();
        Date expectedExpiration = Date.from(LocalDateTime.now()
                .plusSeconds(tokenExpiration)
                .toInstant(ZoneOffset.of(timezoneOffset)));
        // Potential timing variations in test execution
        long toleranceMillisecondsExpiration = 2000;
        long expirationTime = expiration.getTime();
        long expectedExpirationTime = expectedExpiration.getTime();
        assertThat(expirationTime).isCloseTo(expectedExpirationTime,
                within(toleranceMillisecondsExpiration));

        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        verifier.verify(token);
    }

    @Test
    @DisplayName("should retrieve the subject from the token if signature is valid")
    void getSubject_getSubjectValidSignature() {
        String token = JWT.create()
                .withSubject(USER_RECORD.getUsername())
                .withIssuer("JWT")
                .sign(Algorithm.HMAC256(jwtSecret));

        String retrievedSubject = tokenService.getSubject(token);
        assertEquals(USER_RECORD.getUsername(), retrievedSubject);
    }

    @Test
    @DisplayName("should throw SignatureVerificationException " +
            "and not retrieve the subject from the token")
    void getSubject_getSubjectInvalidSignature() {
        String token = JWT.create()
                .withSubject(USER_RECORD.getUsername())
                .sign(Algorithm.HMAC256("invalidSecret"));

        assertThrows(SignatureVerificationException.class, () -> {
            tokenService.getSubject(token);
        });
    }
}