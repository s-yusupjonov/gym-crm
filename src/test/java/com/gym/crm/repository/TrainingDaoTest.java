package com.gym.crm.repository;

import com.gym.crm.domain.Trainee;
import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.Training;
import com.gym.crm.domain.TrainingType;
import com.gym.crm.domain.User;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainingDaoTest {

    private static SessionFactory sessionFactory;

    private TrainingDao trainingDao;
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private TrainingTypeDao trainingTypeDao;

    private Trainee trainee;
    private Trainer trainer;
    private TrainingType cardio;
    private TrainingType yoga;

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
        trainingDao = new TrainingDao(sessionFactory);
        traineeDao = new TraineeDao(sessionFactory);
        trainerDao = new TrainerDao(sessionFactory);
        trainingTypeDao = new TrainingTypeDao(sessionFactory);
        sessionFactory.getCurrentSession().beginTransaction();

        cardio = new TrainingType();
        cardio.setTrainingTypeName("CARDIO");
        trainingTypeDao.save(cardio);

        yoga = new TrainingType();
        yoga.setTrainingTypeName("YOGA");
        trainingTypeDao.save(yoga);

        User traineeUser = new User();
        traineeUser.setFirstName("Nina");
        traineeUser.setLastName("Newman");
        traineeUser.setUsername("nina.newman");
        traineeUser.setPassword("password");
        traineeUser.setActive(true);
        trainee = new Trainee();
        trainee.setUser(traineeUser);
        traineeDao.save(trainee);

        User trainerUser = new User();
        trainerUser.setFirstName("Oscar");
        trainerUser.setLastName("Ortiz");
        trainerUser.setUsername("oscar.ortiz");
        trainerUser.setPassword("password");
        trainerUser.setActive(true);
        trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setSpecialization(cardio);
        trainerDao.save(trainer);

        sessionFactory.getCurrentSession().flush();
    }

    @AfterEach
    void tearDown() {
        sessionFactory.getCurrentSession().getTransaction().rollback();
    }

    private Training newTraining(LocalDate date, TrainingType type, String name) {
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName(name);
        training.setTrainingType(type);
        training.setTrainingDate(date);
        training.setTrainingDuration(60);
        return trainingDao.save(training);
    }

    @Test
    void findTraineeTrainingsShouldReturnAllWhenNoOptionalFiltersProvided() {
        newTraining(LocalDate.of(2026, 1, 10), cardio, "Morning Cardio");
        sessionFactory.getCurrentSession().flush();

        List<Training> found = trainingDao.findTraineeTrainings("nina.newman", null, null, null, null);

        assertEquals(1, found.size());
    }

    @Test
    void findTraineeTrainingsShouldFilterByFromDate() {
        newTraining(LocalDate.of(2026, 1, 1), cardio, "Early");
        newTraining(LocalDate.of(2026, 2, 1), cardio, "Later");
        sessionFactory.getCurrentSession().flush();

        List<Training> found = trainingDao.findTraineeTrainings(
                "nina.newman", LocalDate.of(2026, 1, 15), null, null, null);

        assertEquals(1, found.size());
        assertEquals("Later", found.get(0).getTrainingName());
    }

    @Test
    void findTraineeTrainingsShouldFilterByToDate() {
        newTraining(LocalDate.of(2026, 1, 1), cardio, "Early");
        newTraining(LocalDate.of(2026, 2, 1), cardio, "Later");
        sessionFactory.getCurrentSession().flush();

        List<Training> found = trainingDao.findTraineeTrainings(
                "nina.newman", null, LocalDate.of(2026, 1, 15), null, null);

        assertEquals(1, found.size());
        assertEquals("Early", found.get(0).getTrainingName());
    }

    @Test
    void findTraineeTrainingsShouldFilterByTrainerName() {
        newTraining(LocalDate.of(2026, 1, 1), cardio, "Session");
        sessionFactory.getCurrentSession().flush();

        List<Training> matching = trainingDao.findTraineeTrainings(
                "nina.newman", null, null, "Ortiz", null);
        List<Training> nonMatching = trainingDao.findTraineeTrainings(
                "nina.newman", null, null, "Nobody", null);

        assertEquals(1, matching.size());
        assertTrue(nonMatching.isEmpty());
    }

    @Test
    void findTraineeTrainingsShouldIgnoreBlankTrainerName() {
        newTraining(LocalDate.of(2026, 1, 1), cardio, "Session");
        sessionFactory.getCurrentSession().flush();

        List<Training> found = trainingDao.findTraineeTrainings(
                "nina.newman", null, null, "  ", null);

        assertEquals(1, found.size());
    }

    @Test
    void findTraineeTrainingsShouldFilterByTrainingTypeName() {
        newTraining(LocalDate.of(2026, 1, 1), cardio, "Cardio Session");
        newTraining(LocalDate.of(2026, 1, 2), yoga, "Yoga Session");
        sessionFactory.getCurrentSession().flush();

        List<Training> found = trainingDao.findTraineeTrainings(
                "nina.newman", null, null, null, "YOGA");

        assertEquals(1, found.size());
        assertEquals("Yoga Session", found.get(0).getTrainingName());
    }

    @Test
    void findTraineeTrainingsShouldIgnoreBlankTrainingTypeName() {
        newTraining(LocalDate.of(2026, 1, 1), cardio, "Session");
        sessionFactory.getCurrentSession().flush();

        List<Training> found = trainingDao.findTraineeTrainings(
                "nina.newman", null, null, null, "");

        assertEquals(1, found.size());
    }

    @Test
    void findTrainerTrainingsShouldReturnAllWhenNoOptionalFiltersProvided() {
        newTraining(LocalDate.of(2026, 1, 10), cardio, "Morning Cardio");
        sessionFactory.getCurrentSession().flush();

        List<Training> found = trainingDao.findTrainerTrainings("oscar.ortiz", null, null, null);

        assertEquals(1, found.size());
    }

    @Test
    void findTrainerTrainingsShouldFilterByFromAndToDate() {
        newTraining(LocalDate.of(2026, 1, 1), cardio, "Early");
        newTraining(LocalDate.of(2026, 6, 1), cardio, "Mid");
        newTraining(LocalDate.of(2026, 12, 1), cardio, "Late");
        sessionFactory.getCurrentSession().flush();

        List<Training> found = trainingDao.findTrainerTrainings(
                "oscar.ortiz", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 11, 1), null);

        assertEquals(1, found.size());
        assertEquals("Mid", found.get(0).getTrainingName());
    }

    @Test
    void findTrainerTrainingsShouldFilterByTraineeName() {
        newTraining(LocalDate.of(2026, 1, 1), cardio, "Session");
        sessionFactory.getCurrentSession().flush();

        List<Training> matching = trainingDao.findTrainerTrainings(
                "oscar.ortiz", null, null, "Newman");
        List<Training> nonMatching = trainingDao.findTrainerTrainings(
                "oscar.ortiz", null, null, "Nobody");

        assertEquals(1, matching.size());
        assertTrue(nonMatching.isEmpty());
    }

    @Test
    void findTrainerTrainingsShouldIgnoreBlankTraineeName() {
        newTraining(LocalDate.of(2026, 1, 1), cardio, "Session");
        sessionFactory.getCurrentSession().flush();

        List<Training> found = trainingDao.findTrainerTrainings("oscar.ortiz", null, null, " ");

        assertEquals(1, found.size());
    }
}
