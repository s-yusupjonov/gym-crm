package com.gym.crm.dto.training;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
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
}