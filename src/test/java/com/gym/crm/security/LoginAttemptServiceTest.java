package com.gym.crm.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginAttemptServiceTest {

    private LoginAttemptService loginAttemptService;

    @BeforeEach
    void setUp() {
        loginAttemptService = new LoginAttemptService();
    }

    @Test
    void isBlockedShouldReturnFalseForUserWithNoFailures() {
        assertFalse(loginAttemptService.isBlocked("john.doe"));
    }

    @Test
    void isBlockedShouldReturnFalseAfterFewerThanThreeFailures() {
        loginAttemptService.recordFailure("john.doe");
        loginAttemptService.recordFailure("john.doe");

        assertFalse(loginAttemptService.isBlocked("john.doe"));
    }

    @Test
    void isBlockedShouldReturnTrueAfterThreeConsecutiveFailures() {
        loginAttemptService.recordFailure("john.doe");
        loginAttemptService.recordFailure("john.doe");
        loginAttemptService.recordFailure("john.doe");

        assertTrue(loginAttemptService.isBlocked("john.doe"));
    }

    @Test
    void recordSuccessShouldClearPriorFailures() {
        loginAttemptService.recordFailure("john.doe");
        loginAttemptService.recordFailure("john.doe");

        loginAttemptService.recordSuccess("john.doe");

        loginAttemptService.recordFailure("john.doe");
        loginAttemptService.recordFailure("john.doe");
        assertFalse(loginAttemptService.isBlocked("john.doe"));
    }

    @Test
    void failuresForOneUsernameShouldNotAffectAnother() {
        loginAttemptService.recordFailure("john.doe");
        loginAttemptService.recordFailure("john.doe");
        loginAttemptService.recordFailure("john.doe");

        assertFalse(loginAttemptService.isBlocked("jane.doe"));
    }
}