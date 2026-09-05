package com.gym.crm.service;

import com.gym.crm.dao.TrainingDao;
import com.gym.crm.dao.TrainingTypeDao;
import com.gym.crm.domain.Training;
import com.gym.crm.domain.TrainingType;
import com.gym.crm.exception.ValidationException;
import com.gym.crm.metrics.GymCrmMetrics;
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

    private final TrainingDao trainingDao;
    private final TrainingTypeDao trainingTypeDao;
    private final GymCrmMetrics metrics;

    public TrainingService(TrainingDao trainingDao, TrainingTypeDao trainingTypeDao, GymCrmMetrics metrics) {
        this.trainingDao = trainingDao;
        this.trainingTypeDao = trainingTypeDao;
        this.metrics = metrics;
    }

    @Transactional
    public Training addTraining(Training training) {
        validate(training);

        Training saved = trainingDao.save(training);
        metrics.recordTrainingCreated();
        log.info("Added training: name={}, traineeId={}, trainerId={}",
                saved.getTrainingName(), saved.getTrainee().getId(), saved.getTrainer().getId());

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Training> getTraineeTrainings(String traineeUsername, LocalDate fromDate, LocalDate toDate,
                                              String trainerName, String trainingTypeName) {
        return trainingDao.findTraineeTrainings(traineeUsername, fromDate, toDate, trainerName, trainingTypeName);
    }

    @Transactional(readOnly = true)
    public List<Training> getTrainerTrainings(String trainerUsername, LocalDate fromDate, LocalDate toDate,
                                              String traineeName) {
        return trainingDao.findTrainerTrainings(trainerUsername, fromDate, toDate, traineeName);
    }

    @Transactional(readOnly = true)
    public List<TrainingType> getTrainingTypes() {
        return trainingTypeDao.findAll();
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