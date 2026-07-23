package com.gym.crm.facade;

import com.gym.crm.domain.Trainee;
import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.Training;
import com.gym.crm.service.AuthenticationService;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final AuthenticationService authenticationService;

    @Autowired
    public GymFacade(TraineeService traineeService,
                     TrainerService trainerService,
                     TrainingService trainingService,
                     AuthenticationService authenticationService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.authenticationService = authenticationService;
    }

    public Trainee createTraineeProfile(Trainee trainee) {
        return traineeService.createTraineeProfile(trainee);
    }

    public Trainer createTrainerProfile(Trainer trainer) {
        return trainerService.createTrainerProfile(trainer);
    }

    public boolean matchTraineeCredentials(String username, String password) {
        return authenticationService.matchCredentials(username, password);
    }

    public boolean matchTrainerCredentials(String username, String password) {
        return authenticationService.matchCredentials(username, password);
    }

    public Trainee getTraineeProfile(String username, String password) {
        authenticationService.authenticate(username, password);
        return traineeService.getByUsername(username);
    }

    public Trainer getTrainerProfile(String username, String password) {
        authenticationService.authenticate(username, password);
        return trainerService.getByUsername(username);
    }

    public Trainee updateTraineeProfile(String username, String password, Trainee updates) {
        authenticationService.authenticate(username, password);
        return traineeService.updateTraineeProfile(username, updates);
    }

    public Trainer updateTrainerProfile(String username, String password, Trainer updates) {
        authenticationService.authenticate(username, password);
        return trainerService.updateTrainerProfile(username, updates);
    }

    public void changeTraineePassword(String username, String oldPassword, String newPassword) {
        authenticationService.authenticate(username, oldPassword);
        traineeService.changePassword(username, newPassword);
    }

    public void changeTrainerPassword(String username, String oldPassword, String newPassword) {
        authenticationService.authenticate(username, oldPassword);
        trainerService.changePassword(username, newPassword);
    }

    public void activateDeactivateTrainee(String username, String password, boolean active) {
        authenticationService.authenticate(username, password);
        traineeService.setActive(username, active);
    }

    public void activateDeactivateTrainer(String username, String password, boolean active) {
        authenticationService.authenticate(username, password);
        trainerService.setActive(username, active);
    }

    public void deleteTraineeProfile(String username, String password) {
        authenticationService.authenticate(username, password);
        traineeService.deleteByUsername(username);
    }

    public List<Training> getTraineeTrainings(String username, String password, LocalDate fromDate, LocalDate toDate,
                                              String trainerName, String trainingTypeName) {
        authenticationService.authenticate(username, password);
        return trainingService.getTraineeTrainings(username, fromDate, toDate, trainerName, trainingTypeName);
    }

    public List<Training> getTrainerTrainings(String username, String password, LocalDate fromDate, LocalDate toDate,
                                              String traineeName) {
        authenticationService.authenticate(username, password);
        return trainingService.getTrainerTrainings(username, fromDate, toDate, traineeName);
    }

    public Training addTraining(String traineeUsername, String traineePassword, Training training) {
        authenticationService.authenticate(traineeUsername, traineePassword);
        return trainingService.addTraining(training);
    }

    public List<Trainer> getTrainersNotAssignedToTrainee(String traineeUsername, String password) {
        authenticationService.authenticate(traineeUsername, password);
        return trainerService.getTrainersNotAssignedToTrainee(traineeUsername);
    }

    public List<Trainer> updateTraineeTrainersList(String traineeUsername, String password,
                                                   List<String> trainerUsernames) {
        authenticationService.authenticate(traineeUsername, password);
        return traineeService.updateTrainersList(traineeUsername, trainerUsernames);
    }
}