package com.gym.crm.service;

import com.gym.crm.dao.UserDao;
import com.gym.crm.domain.User;
import com.gym.crm.exception.AuthenticationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserDao userDao;

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
        when(userDao.findByUsername("john.doe")).thenReturn(Optional.of(userWithPassword("secret")));

        assertTrue(authenticationService.matchCredentials("john.doe", "secret"));
    }

    @Test
    void matchCredentialsShouldReturnFalseWhenPasswordDoesNotMatch() {
        when(userDao.findByUsername("john.doe")).thenReturn(Optional.of(userWithPassword("secret")));

        assertFalse(authenticationService.matchCredentials("john.doe", "wrong"));
    }

    @Test
    void matchCredentialsShouldReturnFalseWhenUserNotFound() {
        when(userDao.findByUsername("nobody")).thenReturn(Optional.empty());

        assertFalse(authenticationService.matchCredentials("nobody", "secret"));
    }

    @Test
    void authenticateShouldNotThrowWhenCredentialsMatch() {
        when(userDao.findByUsername("john.doe")).thenReturn(Optional.of(userWithPassword("secret")));

        assertDoesNotThrow(() -> authenticationService.authenticate("john.doe", "secret"));
    }

    @Test
    void authenticateShouldThrowAuthenticationExceptionWhenCredentialsDoNotMatch() {
        when(userDao.findByUsername("john.doe")).thenReturn(Optional.of(userWithPassword("secret")));

        assertThrows(AuthenticationException.class,
                () -> authenticationService.authenticate("john.doe", "wrong"));
    }

    @Test
    void authenticateShouldThrowAuthenticationExceptionWhenUserNotFound() {
        when(userDao.findByUsername("nobody")).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class,
                () -> authenticationService.authenticate("nobody", "secret"));
    }
}
