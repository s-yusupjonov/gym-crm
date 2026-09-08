package com.gym.crm.service;

import com.gym.crm.domain.User;
import com.gym.crm.exception.AccountLockedException;
import com.gym.crm.exception.AuthenticationException;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.metrics.GymCrmMetrics;
import com.gym.crm.repository.UserRepository;
import com.gym.crm.security.JwtService;
import com.gym.crm.security.LoginAttemptService;
import com.gym.crm.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final LoginAttemptService loginAttemptService;
    private final GymCrmMetrics metrics;

    public AuthenticationService(AuthenticationManager authenticationManager, UserRepository userRepository,
                                 PasswordEncoder passwordEncoder, JwtService jwtService,
                                 LoginAttemptService loginAttemptService, GymCrmMetrics metrics) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.loginAttemptService = loginAttemptService;
        this.metrics = metrics;
    }

    public String login(String username, String password) {
        authenticate(username, password);
        return jwtService.generateToken(username);
    }

    public void authenticate(String username, String password) {
        if (loginAttemptService.isBlocked(username)) {
            throw new AccountLockedException("Account is temporarily locked due to too many failed login attempts");
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException | DisabledException ex) {
            loginAttemptService.recordFailure(username);
            metrics.recordAuthenticationFailure();
            log.warn("Authentication failed for username={}", username);
            throw new AuthenticationException("Invalid username or password");
        }

        loginAttemptService.recordSuccess(username);
        metrics.recordAuthenticationSuccess();
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        authenticate(username, oldPassword);

        ValidationUtils.requireNonBlank(newPassword, "New password must not be blank");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
        user.setPassword(passwordEncoder.encode(newPassword));

        log.info("Password changed for username={}", username);
    }
}