package com.gym.crm.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String SECRET = "unit-test-secret-key-that-is-long-enough-for-hs256-signing";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, 60_000L);
    }

    @Test
    void generateTokenShouldEmbedUsernameAsSubject() {
        String token = jwtService.generateToken("john.doe");

        assertEquals("john.doe", jwtService.extractUsername(token));
    }

    @Test
    void generateTokenShouldSetExpirationInTheFuture() {
        String token = jwtService.generateToken("john.doe");

        assertTrue(jwtService.extractExpiration(token).after(new Date()));
    }

    @Test
    void generateTokenShouldAssignUniqueIdToEachToken() {
        String first = jwtService.generateToken("john.doe");
        String second = jwtService.generateToken("john.doe");

        assertNotNull(jwtService.extractJti(first));
        assertNotEquals(jwtService.extractJti(first), jwtService.extractJti(second));
    }

    @Test
    void extractUsernameShouldThrowWhenTokenIsExpired() {
        JwtService shortLivedJwtService = new JwtService(SECRET, -1_000L);
        String expiredToken = shortLivedJwtService.generateToken("john.doe");

        assertThrows(ExpiredJwtException.class, () -> jwtService.extractUsername(expiredToken));
    }

    @Test
    void extractUsernameShouldThrowWhenTokenWasSignedWithDifferentSecret() {
        JwtService otherJwtService = new JwtService(
                "another-unit-test-secret-key-that-is-long-enough-for-hs256", 60_000L);
        String token = otherJwtService.generateToken("john.doe");

        assertThrows(SignatureException.class, () -> jwtService.extractUsername(token));
    }
}