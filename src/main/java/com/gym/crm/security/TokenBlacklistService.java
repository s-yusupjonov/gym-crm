package com.gym.crm.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlacklistService {

    private final Map<String, Instant> blacklistedTokenIds = new ConcurrentHashMap<>();

    public void blacklist(String jti, Date expiresAt) {
        purgeExpired();
        blacklistedTokenIds.put(jti, expiresAt.toInstant());
    }

    public boolean isBlacklisted(String jti) {
        return blacklistedTokenIds.containsKey(jti);
    }

    private void purgeExpired() {
        Instant now = Instant.now();
        blacklistedTokenIds.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
    }
}