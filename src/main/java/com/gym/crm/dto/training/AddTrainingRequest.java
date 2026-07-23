package com.gym.crm.dto.training;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class AddTrainingRequest {

    @NotBlank(message = "Trainee username is required")
    @ApiModelProperty(value = "Trainee username", required = true, example = "john.doe")
    private String traineeUsername;

    @NotBlank(message = "Trainer username is required")
    @ApiModelProperty(value = "Trainer username", required = true, example = "carl.coach")
    private String trainerUsername;

    @NotBlank(message = "Training name is required")
    @ApiModelProperty(value = "Training name", required = true, example = "Morning Cardio")
    private String trainingName;

    @NotNull(message = "Training date is required")
    @ApiModelProperty(value = "Training date", required = true, example = "2026-08-01")
    private LocalDate trainingDate;

    @NotNull(message = "Training duration is required")
    @Positive(message = "Training duration must be a positive number")
    @ApiModelProperty(value = "Training duration in minutes", required = true, example = "60")
    private Integer trainingDuration;

    public String getTraineeUsername() {
        return traineeUsername;
    }

    public void setTraineeUsername(String traineeUsername) {
        this.traineeUsername = traineeUsername;
    }

    public String getTrainerUsername() {
        return trainerUsername;
    }

    public void setTrainerUsername(String trainerUsername) {
        this.trainerUsername = trainerUsername;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public LocalDate getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(LocalDate trainingDate) {
        this.trainingDate = trainingDate;
    }

    public Integer getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(Integer trainingDuration) {
        this.trainingDuration = trainingDuration;
    }
}