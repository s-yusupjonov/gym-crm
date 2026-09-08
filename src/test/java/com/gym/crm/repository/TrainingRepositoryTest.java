package com.gym.crm.repository;

import com.gym.crm.domain.Trainee;
import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.Training;
import com.gym.crm.domain.TrainingType;
import com.gym.crm.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class TrainingRepositoryTest {

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    private Trainee trainee;
    private Trainer trainer;
    private TrainingType cardio;
    private TrainingType yoga;

    @BeforeEach
    void setUp() {
        cardio = new TrainingType();
        cardio.setTrainingTypeName("CARDIO");
        trainingTypeRepository.saveAndFlush(cardio);

        yoga = new TrainingType();
        yoga.setTrainingTypeName("YOGA");
        trainingTypeRepository.saveAndFlush(yoga);

        User traineeUser = new User();
        traineeUser.setFirstName("Nina");
        traineeUser.setLastName("Newman");
        traineeUser.setUsername("nina.newman");
        traineeUser.setPassword("password");
        traineeUser.setActive(true);
        trainee = new Trainee();
        trainee.setUser(traineeUser);
        traineeRepository.saveAndFlush(trainee);

        User trainerUser = new User();
        trainerUser.setFirstName("Oscar");
        trainerUser.setLastName("Ortiz");
        trainerUser.setUsername("oscar.ortiz");
        trainerUser.setPassword("password");
        trainerUser.setActive(true);
        trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setSpecialization(cardio);
        trainerRepository.saveAndFlush(trainer);
    }

    private Training newTraining(LocalDate date, TrainingType type, String name) {
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName(name);
        training.setTrainingType(type);
        training.setTrainingDate(date);
        training.setTrainingDuration(60);
        return trainingRepository.saveAndFlush(training);
    }

    @Test
    void findTraineeTrainingsShouldReturnAllWhenNoOptionalFiltersProvided() {
        newTraining(LocalDate.of(2026, 1, 10), cardio, "Morning Cardio");

        List<Training> found = trainingRepository.findTraineeTrainings("nina.newman", null, null, null, null);

        assertEquals(1, found.size());
    }

    @Test
    void findTraineeTrainingsShouldFilterByFromDate() {
        newTraining(LocalDate.of(2026, 1, 1), cardio, "Early");
        newTraining(LocalDate.of(2026, 2, 1), cardio, "Later");

        List<Training> found = trainingRepository.findTraineeTrainings(
                "nina.newman", LocalDate.of(2026, 1, 15), null, null, null);

        assertEquals(1, found.size());
        assertEquals("Later", found.get(0).getTrainingName());
    }

    @Test
    void findTraineeTrainingsShouldFilterByToDate() {
        newTraining(LocalDate.of(2026, 1, 1), cardio, "Early");
        newTraining(LocalDate.of(2026, 2, 1), cardio, "Later");

        List<Training> found = trainingRepository.findTraineeTrainings(
                "nina.newman", null, LocalDate.of(2026, 1, 15), null, null);

        assertEquals(1, found.size());
        assertEquals("Early", found.get(0).getTrainingName());
    }

    @Test
    void findTraineeTrainingsShouldFilterByTrainerName() {
        newTraining(LocalDate.of(2026, 1, 1), cardio, "Session");

        List<Training> matching = trainingRepository.findTraineeTrainings(
                "nina.newman", null, null, "Ortiz", null);
        List<Training> nonMatching = trainingRepository.findTraineeTrainings(
                "nina.newman", null, null, "Nobody", null);

        assertEquals(1, matching.size());
        assertTrue(nonMatching.isEmpty());
    }

    @Test
    void findTraineeTrainingsShouldFilterByTrainingTypeName() {
        newTraining(LocalDate.of(2026, 1, 1), cardio, "Cardio Session");
        newTraining(LocalDate.of(2026, 1, 2), yoga, "Yoga Session");

        List<Training> found = trainingRepository.findTraineeTrainings(
                "nina.newman", null, null, null, "YOGA");

        assertEquals(1, found.size());
        assertEquals("Yoga Session", found.get(0).getTrainingName());
    }

    @Test
    void findTrainerTrainingsShouldReturnAllWhenNoOptionalFiltersProvided() {
        newTraining(LocalDate.of(2026, 1, 10), cardio, "Morning Cardio");

        List<Training> found = trainingRepository.findTrainerTrainings("oscar.ortiz", null, null, null);

        assertEquals(1, found.size());
    }

    @Test
    void findTrainerTrainingsShouldFilterByFromAndToDate() {
        newTraining(LocalDate.of(2026, 1, 1), cardio, "Early");
        newTraining(LocalDate.of(2026, 6, 1), cardio, "Mid");
        newTraining(LocalDate.of(2026, 12, 1), cardio, "Late");

        List<Training> found = trainingRepository.findTrainerTrainings(
                "oscar.ortiz", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 11, 1), null);

        assertEquals(1, found.size());
        assertEquals("Mid", found.get(0).getTrainingName());
    }

    @Test
    void findTrainerTrainingsShouldFilterByTraineeName() {
        newTraining(LocalDate.of(2026, 1, 1), cardio, "Session");

        List<Training> matching = trainingRepository.findTrainerTrainings(
                "oscar.ortiz", null, null, "Newman");
        List<Training> nonMatching = trainingRepository.findTrainerTrainings(
                "oscar.ortiz", null, null, "Nobody");

        assertEquals(1, matching.size());
        assertTrue(nonMatching.isEmpty());
    }
}