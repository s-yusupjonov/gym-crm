package com.gym.crm.repository;

import com.gym.crm.domain.Trainee;
import com.gym.crm.domain.User;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TraineeDaoTest {

    private static SessionFactory sessionFactory;

    private TraineeDao traineeDao;

    @BeforeAll
    static void initSessionFactory() {
        sessionFactory = TestSessionFactoryFactory.build();
    }

    @AfterAll
    static void closeSessionFactory() {
        sessionFactory.close();
    }

    @BeforeEach
    void setUp() {
        traineeDao = new TraineeDao(sessionFactory);
        sessionFactory.getCurrentSession().beginTransaction();
    }

    @AfterEach
    void tearDown() {
        sessionFactory.getCurrentSession().getTransaction().rollback();
    }

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
        traineeDao.save(newTrainee("alice.smith"));

        Optional<Trainee> found = traineeDao.findByUsername("alice.smith");

        assertTrue(found.isPresent());
        assertEquals("alice.smith", found.get().getUser().getUsername());
    }

    @Test
    void findByUsernameShouldReturnEmptyWhenNotExists() {
        Optional<Trainee> found = traineeDao.findByUsername("nobody");

        assertFalse(found.isPresent());
    }

    @Test
    void deleteShouldRemoveTraineeAndCascadeToUser() {
        Trainee saved = traineeDao.save(newTrainee("bob.jones"));
        sessionFactory.getCurrentSession().flush();

        traineeDao.delete(saved);
        sessionFactory.getCurrentSession().flush();

        assertFalse(traineeDao.findByUsername("bob.jones").isPresent());
    }
}
