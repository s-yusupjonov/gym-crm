package com.gym.crm.service;

import com.gym.crm.domain.User;
import com.gym.crm.exception.AuthenticationException;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.metrics.GymCrmMetrics;
import com.gym.crm.repository.UserRepository;
import com.gym.crm.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserRepository userRepository;
    private final GymCrmMetrics metrics;

    public AuthenticationService(UserRepository userRepository, GymCrmMetrics metrics) {
        this.userRepository = userRepository;
        this.metrics = metrics;
    }

    @Transactional(readOnly = true)
    public boolean matchCredentials(String username, String password) {
        return userRepository.findByUsername(username)
                .map(user -> user.getPassword().equals(password))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public void authenticate(String username, String password) {
        if (!matchCredentials(username, password)) {
            metrics.recordAuthenticationFailure();
            log.warn("Authentication failed for username={}", username);
            throw new AuthenticationException("Invalid username or password");
        }
        metrics.recordAuthenticationSuccess();
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        authenticate(username, oldPassword);

        ValidationUtils.requireNonBlank(newPassword, "New password must not be blank");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
        user.setPassword(newPassword);

        log.info("Password changed for username={}", username);
    }
}