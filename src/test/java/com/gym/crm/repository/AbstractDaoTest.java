package com.gym.crm.repository;

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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractDaoTest {

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
    void saveShouldPersistEntityAndAssignId() {
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("CARDIO");

        TrainingType saved = trainingTypeDao.save(type);

        assertTrue(saved.getId() > 0);
    }

    @Test
    void findByIdShouldReturnEntityWhenExists() {
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("STRENGTH");
        TrainingType saved = trainingTypeDao.save(type);

        Optional<TrainingType> found = trainingTypeDao.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("STRENGTH", found.get().getTrainingTypeName());
    }

    @Test
    void findByIdShouldReturnEmptyWhenNotExists() {
        Optional<TrainingType> found = trainingTypeDao.findById(999L);

        assertFalse(found.isPresent());
    }

    @Test
    void findAllShouldReturnAllPersistedEntities() {
        TrainingType first = new TrainingType();
        first.setTrainingTypeName("YOGA");
        TrainingType second = new TrainingType();
        second.setTrainingTypeName("PILATES");
        trainingTypeDao.save(first);
        trainingTypeDao.save(second);

        List<TrainingType> all = trainingTypeDao.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void updateShouldMergeChanges() {
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("ZUMBA");
        TrainingType saved = trainingTypeDao.save(type);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().detach(saved);

        TrainingType detachedCopy = new TrainingType();
        detachedCopy.setId(saved.getId());
        detachedCopy.setTrainingTypeName("ZUMBA_UPDATED");

        TrainingType merged = trainingTypeDao.update(detachedCopy);

        assertEquals("ZUMBA_UPDATED", merged.getTrainingTypeName());
    }

    @Test
    void deleteShouldRemoveEntity() {
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("CROSSFIT");
        TrainingType saved = trainingTypeDao.save(type);
        sessionFactory.getCurrentSession().flush();

        trainingTypeDao.delete(saved);
        sessionFactory.getCurrentSession().flush();

        Optional<TrainingType> found = trainingTypeDao.findById(saved.getId());
        assertFalse(found.isPresent());
    }
}
