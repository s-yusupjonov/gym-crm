package com.gym.crm.dto.training;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class TrainerTrainingResponse {

    @ApiModelProperty(value = "Training name", example = "Morning Cardio")
    private String trainingName;

    @ApiModelProperty(value = "Training date", example = "2026-08-01")
    private LocalDate trainingDate;

    @ApiModelProperty(value = "Training type name", example = "CARDIO")
    private String trainingType;

    @ApiModelProperty(value = "Training duration in minutes", example = "60")
    private int trainingDuration;

    @ApiModelProperty(value = "Trainee full name", example = "John Doe")
    private String traineeName;
}