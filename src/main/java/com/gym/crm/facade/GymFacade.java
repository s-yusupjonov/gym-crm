package com.gym.crm.facade;

import com.gym.crm.domain.*;
import com.gym.crm.service.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GymFacade {

    private static final Logger log = LoggerFactory.getLogger(GymFacade.class);

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @Autowired
    public GymFacade(
            TraineeService traineeService,
            TrainerService trainerService,
            TrainingService trainingService
    ) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public Trainee createTrainee(Trainee t) {
        return traineeService.create(t);
    }

    public Trainee updateTrainee(Trainee t) {
        return traineeService.update(t);
    }

    public void deleteTrainee(Long id) {
        traineeService.delete(id);
    }

    public Optional<Trainee> getTrainee(Long id) {
        return traineeService.select(id);
    }

    public List<Trainee> getAllTrainees() {
        return traineeService.selectAll();
    }

    public Trainer createTrainer(Trainer t) {
        return trainerService.create(t);
    }

    public Trainer updateTrainer(Trainer t) {
        return trainerService.update(t);
    }

    public Optional<Trainer> getTrainer(Long id) {
        return trainerService.select(id);
    }

    public List<Trainer> getAllTrainers() {
        return trainerService.selectAll();
    }

    public Training createTraining(Training t) {
        return trainingService.create(t);
    }

    public Optional<Training> getTraining(Long id) {
        return trainingService.select(id);
    }

    public List<Training> getAllTrainings() {
        return trainingService.selectAll();
    }
}