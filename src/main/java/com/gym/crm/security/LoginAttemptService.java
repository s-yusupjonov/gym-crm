package com.gym.crm.security;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 3;
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(5);

    private final Map<String, AttemptState> attemptsByUsername = new ConcurrentHashMap<>();

    public void recordFailure(String username) {
        attemptsByUsername.computeIfAbsent(username, key -> new AttemptState()).registerFailure();
    }

    public void recordSuccess(String username) {
        attemptsByUsername.remove(username);
    }

    public boolean isBlocked(String username) {
        AttemptState state = attemptsByUsername.get(username);
        return state != null && state.isBlocked();
    }

    private static final class AttemptState {

        private final AtomicInteger failureCount = new AtomicInteger();
        private volatile Instant blockedUntil;

        void registerFailure() {
            if (failureCount.incrementAndGet() >= MAX_ATTEMPTS) {
                blockedUntil = Instant.now().plus(BLOCK_DURATION);
                failureCount.set(0);
            }
        }

        boolean isBlocked() {
            return blockedUntil != null && Instant.now().isBefore(blockedUntil);
        }
    }
}