package com.gym.crm.service;

import com.gym.crm.client.WorkloadActionType;
import com.gym.crm.client.WorkloadClient;
import com.gym.crm.domain.Trainee;
import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.Training;
import com.gym.crm.domain.TrainingType;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.IllegalTrainingStateException;
import com.gym.crm.exception.ValidationException;
import com.gym.crm.metrics.GymCrmMetrics;
import com.gym.crm.repository.TrainingRepository;
import com.gym.crm.repository.TrainingTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private GymCrmMetrics metrics;

    @Mock
    private WorkloadClient workloadClient;

    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        trainingService = new TrainingService(trainingRepository, trainingTypeRepository, metrics, workloadClient);
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
        when(trainingRepository.save(training)).thenReturn(training);

        Training saved = trainingService.addTraining(training);

        assertEquals(training, saved);
        verify(trainingRepository).save(training);
        verify(metrics).recordTrainingCreated();
    }

    @Test
    void addTrainingShouldNotifyWorkloadServiceWithAddAction() {
        Training training = validTraining();
        when(trainingRepository.save(training)).thenReturn(training);

        trainingService.addTraining(training);

        verify(workloadClient).notify(training, WorkloadActionType.ADD);
    }

    @Test
    void addTrainingShouldSucceedWhenWorkloadClientFails() {
        Training training = validTraining();
        when(trainingRepository.save(training)).thenReturn(training);
        doThrow(new RuntimeException("workload service unavailable"))
                .when(workloadClient).notify(training, WorkloadActionType.ADD);

        Training saved = assertDoesNotThrow(() -> trainingService.addTraining(training));

        assertEquals(training, saved);
        verify(trainingRepository).save(training);
    }

    @Test
    void deleteTrainingShouldThrowWhenTrainingNotFound() {
        when(trainingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> trainingService.deleteTraining(99L));
        verify(trainingRepository, never()).delete(any(Training.class));
    }

    @Test
    void deleteTrainingShouldThrowWhenTrainingDateIsNotInFuture() {
        Training training = validTraining();
        training.setId(5L);
        training.setTrainingDate(LocalDate.now());
        when(trainingRepository.findById(5L)).thenReturn(Optional.of(training));

        assertThrows(IllegalTrainingStateException.class, () -> trainingService.deleteTraining(5L));
        verify(trainingRepository, never()).delete(any(Training.class));
    }

    @Test
    void deleteTrainingShouldThrowWhenTrainingDateIsInThePast() {
        Training training = validTraining();
        training.setId(6L);
        training.setTrainingDate(LocalDate.now().minusDays(1));
        when(trainingRepository.findById(6L)).thenReturn(Optional.of(training));

        assertThrows(IllegalTrainingStateException.class, () -> trainingService.deleteTraining(6L));
        verify(trainingRepository, never()).delete(any(Training.class));
    }

    @Test
    void deleteTrainingShouldDeleteWhenDateIsInTheFuture() {
        Training training = validTraining();
        training.setId(7L);
        training.setTrainingDate(LocalDate.now().plusDays(1));
        when(trainingRepository.findById(7L)).thenReturn(Optional.of(training));

        trainingService.deleteTraining(7L);

        verify(trainingRepository).delete(training);
        verify(metrics).recordTrainingCancelled();
    }

    @Test
    void deleteTrainingShouldNotifyWorkloadServiceWithDeleteAction() {
        Training training = validTraining();
        training.setId(8L);
        training.setTrainingDate(LocalDate.now().plusDays(1));
        when(trainingRepository.findById(8L)).thenReturn(Optional.of(training));

        trainingService.deleteTraining(8L);

        verify(workloadClient).notify(training, WorkloadActionType.DELETE);
    }

    @Test
    void deleteTrainingShouldSucceedWhenWorkloadClientFails() {
        Training training = validTraining();
        training.setId(9L);
        training.setTrainingDate(LocalDate.now().plusDays(1));
        when(trainingRepository.findById(9L)).thenReturn(Optional.of(training));
        doThrow(new RuntimeException("workload service unavailable"))
                .when(workloadClient).notify(training, WorkloadActionType.DELETE);

        assertDoesNotThrow(() -> trainingService.deleteTraining(9L));

        verify(trainingRepository).delete(training);
    }

    @Test
    void getTraineeTrainingsShouldDelegateToRepository() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 2, 1);
        List<Training> trainings = List.of(validTraining());
        when(trainingRepository.findTraineeTrainings("john.doe", from, to, "trainerName", "CARDIO"))
                .thenReturn(trainings);

        List<Training> result = trainingService.getTraineeTrainings(
                "john.doe", from, to, "trainerName", "CARDIO");

        assertEquals(trainings, result);
    }

    @Test
    void getTrainerTrainingsShouldDelegateToRepository() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 2, 1);
        List<Training> trainings = List.of(validTraining());
        when(trainingRepository.findTrainerTrainings("carl.coach", from, to, "traineeName"))
                .thenReturn(trainings);

        List<Training> result = trainingService.getTrainerTrainings("carl.coach", from, to, "traineeName");

        assertEquals(trainings, result);
    }

    @Test
    void getTrainingTypesShouldDelegateToRepository() {
        List<TrainingType> types = List.of(new TrainingType());
        when(trainingTypeRepository.findAll()).thenReturn(types);

        assertEquals(types, trainingService.getTrainingTypes());
    }
}