package com.gym.crm.security;

import com.gym.crm.domain.User;
import com.gym.crm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private GymUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new GymUserDetailsService(userRepository);
    }

    private User activeUser() {
        User user = new User();
        user.setUsername("john.doe");
        user.setPassword("encodedPass");
        user.setActive(true);
        return user;
    }

    @Test
    void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(activeUser()));

        UserDetails userDetails = userDetailsService.loadUserByUsername("john.doe");

        assertEquals("john.doe", userDetails.getUsername());
        assertEquals("encodedPass", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsernameShouldMarkUserDisabledWhenInactive() {
        User inactiveUser = activeUser();
        inactiveUser.setActive(false);
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(inactiveUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("john.doe");

        assertFalse(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsernameShouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername("nobody")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("nobody"));
    }
}