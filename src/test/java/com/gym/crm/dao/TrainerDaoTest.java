package com.gym.crm.dao;

import com.gym.crm.domain.Trainee;
import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.TrainingType;
import com.gym.crm.domain.User;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainerDaoTest {

    private static SessionFactory sessionFactory;

    private TrainerDao trainerDao;
    private TraineeDao traineeDao;
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
        trainerDao = new TrainerDao(sessionFactory);
        traineeDao = new TraineeDao(sessionFactory);
        trainingTypeDao = new TrainingTypeDao(sessionFactory);
        sessionFactory.getCurrentSession().beginTransaction();
    }

    @AfterEach
    void tearDown() {
        sessionFactory.getCurrentSession().getTransaction().rollback();
    }

    private Trainer newTrainer(String username, TrainingType specialization) {
        User user = new User();
        user.setFirstName("Carl");
        user.setLastName("Coach");
        user.setUsername(username);
        user.setPassword("password");
        user.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(specialization);
        return trainer;
    }

    private Trainee newTrainee(String username) {
        User user = new User();
        user.setFirstName("Dana");
        user.setLastName("Dancer");
        user.setUsername(username);
        user.setPassword("password");
        user.setActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        return trainee;
    }

    private TrainingType newTrainingType(String name) {
        TrainingType type = new TrainingType();
        type.setTrainingTypeName(name);
        return trainingTypeDao.save(type);
    }

    @Test
    void findByUsernameShouldReturnTrainerWhenExists() {
        TrainingType type = newTrainingType("CARDIO");
        trainerDao.save(newTrainer("carl.coach", type));

        Optional<Trainer> found = trainerDao.findByUsername("carl.coach");

        assertTrue(found.isPresent());
        assertEquals("carl.coach", found.get().getUser().getUsername());
    }

    @Test
    void findByUsernameShouldReturnEmptyWhenNotExists() {
        Optional<Trainer> found = trainerDao.findByUsername("nobody");

        assertFalse(found.isPresent());
    }

    @Test
    void findByUsernamesShouldReturnMatchingTrainers() {
        TrainingType type = newTrainingType("STRENGTH");
        trainerDao.save(newTrainer("t1", type));
        trainerDao.save(newTrainer("t2", type));
        trainerDao.save(newTrainer("t3", type));

        List<Trainer> found = trainerDao.findByUsernames(List.of("t1", "t3"));

        assertEquals(2, found.size());
    }

    @Test
    void findByUsernamesShouldReturnEmptyListWhenNoneMatch() {
        List<Trainer> found = trainerDao.findByUsernames(List.of("nobody"));

        assertTrue(found.isEmpty());
    }

    @Test
    void findNotAssignedToTraineeShouldExcludeAssignedTrainers() {
        TrainingType type = newTrainingType("YOGA");
        Trainer assigned = trainerDao.save(newTrainer("assigned.trainer", type));
        Trainer unassigned = trainerDao.save(newTrainer("unassigned.trainer", type));

        Trainee trainee = newTrainee("dana.dancer");
        Set<Trainer> trainers = new HashSet<>();
        trainers.add(assigned);
        trainee.setTrainers(trainers);
        traineeDao.save(trainee);
        sessionFactory.getCurrentSession().flush();

        List<Trainer> notAssigned = trainerDao.findNotAssignedToTrainee("dana.dancer");

        assertEquals(1, notAssigned.size());
        assertEquals(unassigned.getUser().getUsername(), notAssigned.get(0).getUser().getUsername());
    }

    @Test
    void findNotAssignedToTraineeShouldReturnAllTrainersWhenNoneAssigned() {
        TrainingType type = newTrainingType("PILATES");
        trainerDao.save(newTrainer("free.trainer", type));

        Trainee trainee = newTrainee("empty.trainee");
        traineeDao.save(trainee);
        sessionFactory.getCurrentSession().flush();

        List<Trainer> notAssigned = trainerDao.findNotAssignedToTrainee("empty.trainee");

        assertEquals(1, notAssigned.size());
    }
}
