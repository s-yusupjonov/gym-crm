package com.gym.crm.mapper;

import com.gym.crm.domain.Trainee;
import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.TrainingType;
import com.gym.crm.dto.common.TraineeShortDto;
import com.gym.crm.dto.common.TrainerShortDto;
import com.gym.crm.dto.common.TrainingTypeDto;

import java.util.List;

public final class ReferenceMapper {

    private ReferenceMapper() {
    }

    public static TrainingTypeDto toTrainingTypeDto(TrainingType trainingType) {
        if (trainingType == null) {
            return null;
        }
        return new TrainingTypeDto(trainingType.getId(), trainingType.getTrainingTypeName());
    }

    public static TrainerShortDto toTrainerShortDto(Trainer trainer) {
        TrainerShortDto dto = new TrainerShortDto();
        dto.setUsername(trainer.getUser().getUsername());
        dto.setFirstName(trainer.getUser().getFirstName());
        dto.setLastName(trainer.getUser().getLastName());
        dto.setSpecialization(toTrainingTypeDto(trainer.getSpecialization()));
        return dto;
    }

    public static List<TrainerShortDto> toTrainerShortDtoList(List<Trainer> trainers) {
        return trainers.stream().map(ReferenceMapper::toTrainerShortDto).toList();
    }

    public static TraineeShortDto toTraineeShortDto(Trainee trainee) {
        TraineeShortDto dto = new TraineeShortDto();
        dto.setUsername(trainee.getUser().getUsername());
        dto.setFirstName(trainee.getUser().getFirstName());
        dto.setLastName(trainee.getUser().getLastName());
        return dto;
    }

    public static List<TraineeShortDto> toTraineeShortDtoList(List<Trainee> trainees) {
        return trainees.stream().map(ReferenceMapper::toTraineeShortDto).toList();
    }
}