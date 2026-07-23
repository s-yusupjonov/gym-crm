package com.gym.crm.dto.training;

import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDate;

/**
 * Response item for "Get Trainee Trainings List". Carries the trainer's name rather than
 * the trainer's full profile, per spec.
 */
public class TraineeTrainingResponse {

    @ApiModelProperty(value = "Training name", example = "Morning Cardio")
    private String trainingName;

    @ApiModelProperty(value = "Training date", example = "2026-08-01")
    private LocalDate trainingDate;

    @ApiModelProperty(value = "Training type name", example = "CARDIO")
    private String trainingType;

    @ApiModelProperty(value = "Training duration in minutes", example = "60")
    private int trainingDuration;

    @ApiModelProperty(value = "Trainer full name", example = "Carl Coach")
    private String trainerName;

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

    public String getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(String trainingType) {
        this.trainingType = trainingType;
    }

    public int getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(int trainingDuration) {
        this.trainingDuration = trainingDuration;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }
}