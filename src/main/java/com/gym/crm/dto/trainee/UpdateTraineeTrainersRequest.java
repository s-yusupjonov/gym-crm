package com.gym.crm.dto.trainee;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UpdateTraineeTrainersRequest {

    @NotEmpty(message = "Trainers list must not be empty")
    @Valid
    @ApiModelProperty(value = "New full list of trainers assigned to the trainee", required = true)
    private List<TrainerUsername> trainers;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TrainerUsername {

        @NotBlank(message = "Trainer username is required")
        @ApiModelProperty(value = "Trainer username", required = true, example = "carl.coach")
        private String trainerUsername;
    }
}