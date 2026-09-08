package com.gym.crm.service;

import com.gym.crm.domain.User;
import com.gym.crm.exception.AuthenticationException;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.ValidationException;
import com.gym.crm.metrics.GymCrmMetrics;
import com.gym.crm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GymCrmMetrics metrics;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User userWithPassword(String password) {
        User user = new User();
        user.setUsername("john.doe");
        user.setPassword(password);
        return user;
    }

    @Test
    void matchCredentialsShouldReturnTrueWhenPasswordMatches() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(userWithPassword("secret")));

        assertTrue(authenticationService.matchCredentials("john.doe", "secret"));
    }

    @Test
    void matchCredentialsShouldReturnFalseWhenPasswordDoesNotMatch() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(userWithPassword("secret")));

        assertFalse(authenticationService.matchCredentials("john.doe", "wrong"));
    }

    @Test
    void matchCredentialsShouldReturnFalseWhenUserNotFound() {
        when(userRepository.findByUsername("nobody")).thenReturn(Optional.empty());

        assertFalse(authenticationService.matchCredentials("nobody", "secret"));
    }

    @Test
    void authenticateShouldNotThrowWhenCredentialsMatch() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(userWithPassword("secret")));

        assertDoesNotThrow(() -> authenticationService.authenticate("john.doe", "secret"));
    }

    @Test
    void authenticateShouldRecordSuccessMetricWhenCredentialsMatch() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(userWithPassword("secret")));

        authenticationService.authenticate("john.doe", "secret");

        verify(metrics).recordAuthenticationSuccess();
        verify(metrics, never()).recordAuthenticationFailure();
    }

    @Test
    void authenticateShouldThrowAuthenticationExceptionWhenCredentialsDoNotMatch() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(userWithPassword("secret")));

        assertThrows(AuthenticationException.class,
                () -> authenticationService.authenticate("john.doe", "wrong"));
    }

    @Test
    void authenticateShouldRecordFailureMetricWhenCredentialsDoNotMatch() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(userWithPassword("secret")));

        assertThrows(AuthenticationException.class,
                () -> authenticationService.authenticate("john.doe", "wrong"));

        verify(metrics).recordAuthenticationFailure();
        verify(metrics, never()).recordAuthenticationSuccess();
    }

    @Test
    void authenticateShouldThrowAuthenticationExceptionWhenUserNotFound() {
        when(userRepository.findByUsername("nobody")).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class,
                () -> authenticationService.authenticate("nobody", "secret"));
    }

    @Test
    void changePasswordShouldThrowAuthenticationExceptionWhenOldCredentialsAreInvalid() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(userWithPassword("secret")));

        assertThrows(AuthenticationException.class,
                () -> authenticationService.changePassword("john.doe", "wrongOld", "newPass"));
    }

    @Test
    void changePasswordShouldThrowValidationExceptionWhenNewPasswordIsNull() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(userWithPassword("secret")));

        assertThrows(ValidationException.class,
                () -> authenticationService.changePassword("john.doe", "secret", null));
    }

    @Test
    void changePasswordShouldThrowValidationExceptionWhenNewPasswordIsBlank() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(userWithPassword("secret")));

        assertThrows(ValidationException.class,
                () -> authenticationService.changePassword("john.doe", "secret", "   "));
    }

    @Test
    void changePasswordShouldThrowEntityNotFoundExceptionWhenUserDisappearsBetweenLookups() {
        User user = userWithPassword("secret");
        when(userRepository.findByUsername("john.doe"))
                .thenReturn(Optional.of(user))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> authenticationService.changePassword("john.doe", "secret", "newPass"));
    }

    @Test
    void changePasswordShouldUpdatePasswordWhenCredentialsAndNewPasswordAreValid() {
        User user = userWithPassword("secret");
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

        authenticationService.changePassword("john.doe", "secret", "newPass");

        assertEquals("newPass", user.getPassword());
    }
}