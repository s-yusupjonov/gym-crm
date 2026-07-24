package com.gym.crm.mapper;

import com.gym.crm.domain.Trainee;
import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.Training;
import com.gym.crm.dto.training.AddTrainingRequest;
import com.gym.crm.dto.training.TraineeTrainingResponse;
import com.gym.crm.dto.training.TrainerTrainingResponse;

import java.util.List;

public final class TrainingMapper {

    private TrainingMapper() {
    }

    public static Training toEntity(AddTrainingRequest request, Trainee trainee, Trainer trainer) {
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName(request.getTrainingName());
        training.setTrainingDate(request.getTrainingDate());
        training.setTrainingDuration(request.getTrainingDuration());
        training.setTrainingType(trainer.getSpecialization());
        return training;
    }

    public static TraineeTrainingResponse toTraineeTrainingResponse(Training training) {
        TraineeTrainingResponse response = new TraineeTrainingResponse();
        response.setTrainingName(training.getTrainingName());
        response.setTrainingDate(training.getTrainingDate());
        response.setTrainingType(training.getTrainingType().getTrainingTypeName());
        response.setTrainingDuration(training.getTrainingDuration());
        response.setTrainerName(fullName(training.getTrainer().getUser().getFirstName(),
                training.getTrainer().getUser().getLastName()));
        return response;
    }

    public static List<TraineeTrainingResponse> toTraineeTrainingResponseList(List<Training> trainings) {
        return trainings.stream().map(TrainingMapper::toTraineeTrainingResponse).toList();
    }

    public static TrainerTrainingResponse toTrainerTrainingResponse(Training training) {
        TrainerTrainingResponse response = new TrainerTrainingResponse();
        response.setTrainingName(training.getTrainingName());
        response.setTrainingDate(training.getTrainingDate());
        response.setTrainingType(training.getTrainingType().getTrainingTypeName());
        response.setTrainingDuration(training.getTrainingDuration());
        response.setTraineeName(fullName(training.getTrainee().getUser().getFirstName(),
                training.getTrainee().getUser().getLastName()));
        return response;
    }

    public static List<TrainerTrainingResponse> toTrainerTrainingResponseList(List<Training> trainings) {
        return trainings.stream().map(TrainingMapper::toTrainerTrainingResponse).toList();
    }

    private static String fullName(String firstName, String lastName) {
        return firstName + " " + lastName;
    }
}