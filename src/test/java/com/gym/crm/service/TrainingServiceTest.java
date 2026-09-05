package com.gym.crm.service;

import com.gym.crm.dao.TrainingDao;
import com.gym.crm.dao.TrainingTypeDao;
import com.gym.crm.domain.Trainee;
import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.Training;
import com.gym.crm.domain.TrainingType;
import com.gym.crm.exception.ValidationException;
import com.gym.crm.metrics.GymCrmMetrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @Mock
    private GymCrmMetrics metrics;

    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        trainingService = new TrainingService(trainingDao, trainingTypeDao, metrics);
    }

    private Training validTraining() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        Trainer trainer = new Trainer();
        trainer.setId(2L);

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName("Morning Session");
        training.setTrainingType(new TrainingType());
        training.setTrainingDate(LocalDate.of(2026, 1, 1));
        training.setTrainingDuration(60);
        return training;
    }

    @Test
    void addTrainingShouldThrowWhenTraineeIsNull() {
        Training training = validTraining();
        training.setTrainee(null);

        assertThrows(ValidationException.class, () -> trainingService.addTraining(training));
    }

    @Test
    void addTrainingShouldThrowWhenTrainerIsNull() {
        Training training = validTraining();
        training.setTrainer(null);

        assertThrows(ValidationException.class, () -> trainingService.addTraining(training));
    }

    @Test
    void addTrainingShouldThrowWhenTrainingNameIsNull() {
        Training training = validTraining();
        training.setTrainingName(null);

        assertThrows(ValidationException.class, () -> trainingService.addTraining(training));
    }

    @Test
    void addTrainingShouldThrowWhenTrainingNameIsBlank() {
        Training training = validTraining();
        training.setTrainingName("   ");

        assertThrows(ValidationException.class, () -> trainingService.addTraining(training));
    }

    @Test
    void addTrainingShouldThrowWhenTrainingTypeIsNull() {
        Training training = validTraining();
        training.setTrainingType(null);

        assertThrows(ValidationException.class, () -> trainingService.addTraining(training));
    }

    @Test
    void addTrainingShouldThrowWhenTrainingDateIsNull() {
        Training training = validTraining();
        training.setTrainingDate(null);

        assertThrows(ValidationException.class, () -> trainingService.addTraining(training));
    }

    @Test
    void addTrainingShouldThrowWhenDurationIsZero() {
        Training training = validTraining();
        training.setTrainingDuration(0);

        assertThrows(ValidationException.class, () -> trainingService.addTraining(training));
    }

    @Test
    void addTrainingShouldThrowWhenDurationIsNegative() {
        Training training = validTraining();
        training.setTrainingDuration(-5);

        assertThrows(ValidationException.class, () -> trainingService.addTraining(training));
    }

    @Test
    void addTrainingShouldSaveWhenValid() {
        Training training = validTraining();
        when(trainingDao.save(training)).thenReturn(training);

        Training saved = trainingService.addTraining(training);

        assertEquals(training, saved);
        verify(trainingDao).save(training);
        verify(metrics).recordTrainingCreated();
    }

    @Test
    void getTraineeTrainingsShouldDelegateToDao() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 2, 1);
        List<Training> trainings = List.of(validTraining());
        when(trainingDao.findTraineeTrainings("john.doe", from, to, "trainerName", "CARDIO"))
                .thenReturn(trainings);

        List<Training> result = trainingService.getTraineeTrainings(
                "john.doe", from, to, "trainerName", "CARDIO");

        assertEquals(trainings, result);
    }

    @Test
    void getTrainerTrainingsShouldDelegateToDao() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 2, 1);
        List<Training> trainings = List.of(validTraining());
        when(trainingDao.findTrainerTrainings("carl.coach", from, to, "traineeName"))
                .thenReturn(trainings);

        List<Training> result = trainingService.getTrainerTrainings("carl.coach", from, to, "traineeName");

        assertEquals(trainings, result);
    }

    @Test
    void getTrainingTypesShouldDelegateToDao() {
        List<TrainingType> types = List.of(new TrainingType());
        when(trainingTypeDao.findAll()).thenReturn(types);

        assertEquals(types, trainingService.getTrainingTypes());
    }
}