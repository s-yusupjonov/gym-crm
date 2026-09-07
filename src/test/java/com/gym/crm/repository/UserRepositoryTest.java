package com.gym.crm.repository;

import com.gym.crm.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User newUser(String username) {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername(username);
        user.setPassword("password");
        user.setActive(true);
        return user;
    }

    @Test
    void findByUsernameShouldReturnUserWhenExists() {
        userRepository.saveAndFlush(newUser("john.doe"));

        Optional<User> found = userRepository.findByUsername("john.doe");

        assertTrue(found.isPresent());
        assertEquals("john.doe", found.get().getUsername());
    }

    @Test
    void findByUsernameShouldReturnEmptyWhenNotExists() {
        Optional<User> found = userRepository.findByUsername("nobody");

        assertFalse(found.isPresent());
    }

    @Test
    void existsByUsernameShouldReturnTrueWhenExists() {
        userRepository.saveAndFlush(newUser("jane.doe"));

        assertTrue(userRepository.existsByUsername("jane.doe"));
    }

    @Test
    void existsByUsernameShouldReturnFalseWhenNotExists() {
        assertFalse(userRepository.existsByUsername("nobody"));
    }
}