package com.gym.crm.service;

import com.gym.crm.client.WorkloadActionType;
import com.gym.crm.client.WorkloadClient;
import com.gym.crm.domain.Training;
import com.gym.crm.domain.TrainingType;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.IllegalTrainingStateException;
import com.gym.crm.exception.ValidationException;
import com.gym.crm.metrics.GymCrmMetrics;
import com.gym.crm.repository.TrainingRepository;
import com.gym.crm.repository.TrainingTypeRepository;
import com.gym.crm.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingService.class);

    private final TrainingRepository trainingRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final GymCrmMetrics metrics;
    private final WorkloadClient workloadClient;

    public TrainingService(TrainingRepository trainingRepository, TrainingTypeRepository trainingTypeRepository,
                           GymCrmMetrics metrics, WorkloadClient workloadClient) {
        this.trainingRepository = trainingRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.metrics = metrics;
        this.workloadClient = workloadClient;
    }

    @Transactional
    public Training addTraining(Training training) {
        validate(training);

        Training saved = trainingRepository.save(training);
        metrics.recordTrainingCreated();
        log.info("Added training: name={}, traineeId={}, trainerId={}",
                saved.getTrainingName(), saved.getTrainee().getId(), saved.getTrainer().getId());

        notifyWorkloadService(saved, WorkloadActionType.ADD);

        return saved;
    }

    @Transactional
    public void deleteTraining(Long id) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Training not found: " + id));

        if (!training.getTrainingDate().isAfter(LocalDate.now())) {
            throw new IllegalTrainingStateException("Cannot cancel a training that has already occurred");
        }

        trainingRepository.delete(training);
        metrics.recordTrainingCancelled();
        log.info("Cancelled training: id={}, name={}", training.getId(), training.getTrainingName());

        notifyWorkloadService(training, WorkloadActionType.DELETE);
    }

    @Transactional(readOnly = true)
    public List<Training> getTraineeTrainings(String traineeUsername, LocalDate fromDate, LocalDate toDate,
                                              String trainerName, String trainingTypeName) {
        return trainingRepository.findTraineeTrainings(traineeUsername, fromDate, toDate, trainerName, trainingTypeName);
    }

    @Transactional(readOnly = true)
    public List<Training> getTrainerTrainings(String trainerUsername, LocalDate fromDate, LocalDate toDate,
                                              String traineeName) {
        return trainingRepository.findTrainerTrainings(trainerUsername, fromDate, toDate, traineeName);
    }

    @Transactional(readOnly = true)
    public List<TrainingType> getTrainingTypes() {
        return trainingTypeRepository.findAll();
    }

    private void notifyWorkloadService(Training training, WorkloadActionType actionType) {
        try {
            workloadClient.notify(training, actionType);
        } catch (Exception ex) {
            log.warn("Unexpected failure notifying trainer-workload-service: trainingId={} actionType={} reason={}",
                    training.getId(), actionType, ex.getMessage());
        }
    }

    private void validate(Training training) {
        if (training.getTrainee() == null) {
            throw new ValidationException("Trainee is required");
        }
        if (training.getTrainer() == null) {
            throw new ValidationException("Trainer is required");
        }
        if (ValidationUtils.isBlank(training.getTrainingName())) {
            throw new ValidationException("Training name is required");
        }
        if (training.getTrainingType() == null) {
            throw new ValidationException("Training type is required");
        }
        if (training.getTrainingDate() == null) {
            throw new ValidationException("Training date is required");
        }
        if (training.getTrainingDuration() <= 0) {
            throw new ValidationException("Training duration must be positive");
        }
    }
}