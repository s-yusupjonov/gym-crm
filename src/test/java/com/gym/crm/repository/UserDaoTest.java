package com.gym.crm.repository;

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

class UserDaoTest {

    private static SessionFactory sessionFactory;

    private UserDao userDao;

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
        userDao = new UserDao(sessionFactory);
        sessionFactory.getCurrentSession().beginTransaction();
    }

    @AfterEach
    void tearDown() {
        sessionFactory.getCurrentSession().getTransaction().rollback();
    }

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
        userDao.save(newUser("john.doe"));

        Optional<User> found = userDao.findByUsername("john.doe");

        assertTrue(found.isPresent());
        assertEquals("john.doe", found.get().getUsername());
    }

    @Test
    void findByUsernameShouldReturnEmptyWhenNotExists() {
        Optional<User> found = userDao.findByUsername("nobody");

        assertFalse(found.isPresent());
    }

    @Test
    void existsByUsernameShouldReturnTrueWhenExists() {
        userDao.save(newUser("jane.doe"));

        assertTrue(userDao.existsByUsername("jane.doe"));
    }

    @Test
    void existsByUsernameShouldReturnFalseWhenNotExists() {
        assertFalse(userDao.existsByUsername("nobody"));
    }
}
