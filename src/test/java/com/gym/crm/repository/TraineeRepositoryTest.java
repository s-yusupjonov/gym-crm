package com.gym.crm.repository;

import com.gym.crm.domain.Trainee;
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
class TraineeRepositoryTest {

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private UserRepository userRepository;

    private Trainee newTrainee(String username) {
        User user = new User();
        user.setFirstName("Alice");
        user.setLastName("Smith");
        user.setUsername(username);
        user.setPassword("password");
        user.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        return trainee;
    }

    @Test
    void findByUsernameShouldReturnTraineeWhenExists() {
        traineeRepository.saveAndFlush(newTrainee("alice.smith"));

        Optional<Trainee> found = traineeRepository.findByUsername("alice.smith");

        assertTrue(found.isPresent());
        assertEquals("alice.smith", found.get().getUser().getUsername());
    }

    @Test
    void findByUsernameShouldReturnEmptyWhenNotExists() {
        Optional<Trainee> found = traineeRepository.findByUsername("nobody");

        assertFalse(found.isPresent());
    }

    @Test
    void deleteShouldRemoveTraineeAndCascadeToUser() {
        Trainee saved = traineeRepository.saveAndFlush(newTrainee("bob.jones"));
        Long userId = saved.getUser().getId();

        traineeRepository.delete(saved);
        traineeRepository.flush();

        assertFalse(traineeRepository.findByUsername("bob.jones").isPresent());
        assertFalse(userRepository.findById(userId).isPresent());
    }
}