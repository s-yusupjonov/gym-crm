package com.gym.crm.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenBlacklistServiceTest {

    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setUp() {
        tokenBlacklistService = new TokenBlacklistService();
    }

    @Test
    void isBlacklistedShouldReturnFalseForUnknownToken() {
        assertFalse(tokenBlacklistService.isBlacklisted("unknown-jti"));
    }

    @Test
    void isBlacklistedShouldReturnTrueAfterBlacklisting() {
        Date expiresAt = new Date(System.currentTimeMillis() + 60_000);

        tokenBlacklistService.blacklist("token-id", expiresAt);

        assertTrue(tokenBlacklistService.isBlacklisted("token-id"));
    }

    @Test
    void isBlacklistedShouldReturnFalseOnceTokenHasExpired() {
        Date alreadyExpired = new Date(System.currentTimeMillis() - 1_000);

        tokenBlacklistService.blacklist("token-id", alreadyExpired);

        assertFalse(tokenBlacklistService.isBlacklisted("token-id"));
    }

    @Test
    void blacklistShouldPurgeExpiredEntriesWhenCalledAgain() {
        Date alreadyExpired = new Date(System.currentTimeMillis() - 1_000);
        Date stillValid = new Date(System.currentTimeMillis() + 60_000);

        tokenBlacklistService.blacklist("expired-token", alreadyExpired);
        tokenBlacklistService.blacklist("fresh-token", stillValid);

        assertFalse(tokenBlacklistService.isBlacklisted("expired-token"));
        assertTrue(tokenBlacklistService.isBlacklisted("fresh-token"));
    }
}