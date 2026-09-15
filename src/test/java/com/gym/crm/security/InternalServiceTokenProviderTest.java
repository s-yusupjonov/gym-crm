package com.gym.crm.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InternalServiceTokenProviderTest {

    private static final String SECRET = "unit-test-internal-secret-key-that-is-long-enough-for-hs256";

    private InternalServiceTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new InternalServiceTokenProvider(SECRET, 60_000L);
    }

    @Test
    void generateTokenShouldUseGymCrmAsSubject() {
        String token = tokenProvider.generateToken();

        String subject = parseSubject(token, SECRET);

        assertEquals("gym-crm", subject);
    }

    @Test
    void generateTokenShouldBeSignedWithConfiguredSecret() {
        String token = tokenProvider.generateToken();

        assertEquals("gym-crm", parseSubject(token, SECRET));
    }

    @Test
    void generateTokenShouldRejectWhenVerifiedWithDifferentSecret() {
        String token = tokenProvider.generateToken();

        assertThrows(io.jsonwebtoken.security.SignatureException.class,
                () -> parseSubject(token, "a-completely-different-unit-test-secret-key-value"));
    }

    @Test
    void generateTokenShouldExpireQuickly() {
        String token = tokenProvider.generateToken();

        var claims = Jwts.parser()
                .verifyWith(secretKey(SECRET))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        long ttlMs = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();
        assertTrue(ttlMs <= 60_000L);
    }

    private static String parseSubject(String token, String secret) {
        return Jwts.parser()
                .verifyWith(secretKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    private static SecretKey secretKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}