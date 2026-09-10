package com.gym.crm.service;

import com.gym.crm.domain.User;
import com.gym.crm.exception.AccountLockedException;
import com.gym.crm.exception.AuthenticationException;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.ValidationException;
import com.gym.crm.metrics.GymCrmMetrics;
import com.gym.crm.repository.UserRepository;
import com.gym.crm.security.JwtService;
import com.gym.crm.security.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private GymCrmMetrics metrics;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(authenticationManager, userRepository, passwordEncoder,
                jwtService, loginAttemptService, metrics);
    }

    private User userWithPassword(String encodedPassword) {
        User user = new User();
        user.setUsername("john.doe");
        user.setPassword(encodedPassword);
        return user;
    }

    @Test
    void authenticateShouldNotThrowWhenCredentialsAreValid() {
        assertDoesNotThrow(() -> authenticationService.authenticate("john.doe", "secret"));

        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken("john.doe", "secret"));
    }

    @Test
    void authenticateShouldRecordSuccessMetricAndResetAttemptsWhenCredentialsAreValid() {
        authenticationService.authenticate("john.doe", "secret");

        verify(loginAttemptService).recordSuccess("john.doe");
        verify(metrics).recordAuthenticationSuccess();
        verify(metrics, never()).recordAuthenticationFailure();
    }

    @Test
    void authenticateShouldThrowAuthenticationExceptionWhenCredentialsAreInvalid() {
        doThrow(new BadCredentialsException("bad credentials"))
                .when(authenticationManager).authenticate(any());

        assertThrows(AuthenticationException.class,
                () -> authenticationService.authenticate("john.doe", "wrong"));
    }

    @Test
    void authenticateShouldRecordFailureAndMetricWhenCredentialsAreInvalid() {
        doThrow(new BadCredentialsException("bad credentials"))
                .when(authenticationManager).authenticate(any());

        assertThrows(AuthenticationException.class,
                () -> authenticationService.authenticate("john.doe", "wrong"));

        verify(loginAttemptService).recordFailure("john.doe");
        verify(metrics).recordAuthenticationFailure();
        verify(metrics, never()).recordAuthenticationSuccess();
    }

    @Test
    void authenticateShouldThrowAccountLockedExceptionWhenUserIsBlocked() {
        when(loginAttemptService.isBlocked("john.doe")).thenReturn(true);

        assertThrows(AccountLockedException.class,
                () -> authenticationService.authenticate("john.doe", "secret"));

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void loginShouldReturnTokenWhenCredentialsAreValid() {
        when(jwtService.generateToken("john.doe")).thenReturn("jwt-token");

        String token = authenticationService.login("john.doe", "secret");

        assertEquals("jwt-token", token);
        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken("john.doe", "secret"));
    }

    @Test
    void loginShouldThrowWhenCredentialsAreInvalid() {
        doThrow(new BadCredentialsException("bad credentials"))
                .when(authenticationManager).authenticate(any());

        assertThrows(AuthenticationException.class, () -> authenticationService.login("john.doe", "wrong"));
    }

    @Test
    void changePasswordShouldThrowWhenOldCredentialsAreInvalid() {
        doThrow(new BadCredentialsException("bad credentials"))
                .when(authenticationManager).authenticate(any());

        assertThrows(AuthenticationException.class,
                () -> authenticationService.changePassword("john.doe", "wrongOld", "newPass"));

        verify(userRepository, never()).findByUsername(any());
    }

    @Test
    void changePasswordShouldThrowValidationExceptionWhenNewPasswordIsNull() {
        assertThrows(ValidationException.class,
                () -> authenticationService.changePassword("john.doe", "secret", null));
    }

    @Test
    void changePasswordShouldThrowValidationExceptionWhenNewPasswordIsBlank() {
        assertThrows(ValidationException.class,
                () -> authenticationService.changePassword("john.doe", "secret", "   "));
    }

    @Test
    void changePasswordShouldThrowEntityNotFoundExceptionWhenUserDisappearsAfterAuthentication() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> authenticationService.changePassword("john.doe", "secret", "newPass"));
    }

    @Test
    void changePasswordShouldEncodeAndSaveNewPasswordWhenCredentialsAreValid() {
        User user = userWithPassword("oldEncodedPass");
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass")).thenReturn("newEncodedPass");

        authenticationService.changePassword("john.doe", "secret", "newPass");

        assertEquals("newEncodedPass", user.getPassword());
        verify(authenticationManager).authenticate(eq(new UsernamePasswordAuthenticationToken("john.doe", "secret")));
    }
}