package com.gym.crm.dao;

import com.gym.crm.domain.TrainingType;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainingTypeDaoTest {

    private static SessionFactory sessionFactory;

    private TrainingTypeDao trainingTypeDao;

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
        trainingTypeDao = new TrainingTypeDao(sessionFactory);
        sessionFactory.getCurrentSession().beginTransaction();
    }

    @AfterEach
    void tearDown() {
        sessionFactory.getCurrentSession().getTransaction().rollback();
    }

    @Test
    void saveAndFindByIdShouldRoundTrip() {
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("CARDIO");

        TrainingType saved = trainingTypeDao.save(type);
        Optional<TrainingType> found = trainingTypeDao.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("CARDIO", found.get().getTrainingTypeName());
    }

    @Test
    void findAllShouldReturnEveryPersistedTrainingType() {
        TrainingType first = new TrainingType();
        first.setTrainingTypeName("STRENGTH");
        TrainingType second = new TrainingType();
        second.setTrainingTypeName("YOGA");

        trainingTypeDao.save(first);
        trainingTypeDao.save(second);

        List<TrainingType> all = trainingTypeDao.findAll();

        assertEquals(2, all.size());
    }
}
