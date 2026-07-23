package com.gym.crm.dto.trainee;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class UpdateTraineeTrainersRequest {

    @NotEmpty(message = "Trainers list must not be empty")
    @Valid
    @ApiModelProperty(value = "New full list of trainers assigned to the trainee", required = true)
    private List<TrainerUsername> trainers;

    public List<TrainerUsername> getTrainers() {
        return trainers;
    }

    public void setTrainers(List<TrainerUsername> trainers) {
        this.trainers = trainers;
    }

    public static class TrainerUsername {

        @NotBlank(message = "Trainer username is required")
        @ApiModelProperty(value = "Trainer username", required = true, example = "carl.coach")
        private String trainerUsername;

        public String getTrainerUsername() {
            return trainerUsername;
        }

        public void setTrainerUsername(String trainerUsername) {
            this.trainerUsername = trainerUsername;
        }
    }
}