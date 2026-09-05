package com.gym.crm.dto.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainerShortDto {

    @ApiModelProperty(value = "Trainer username", example = "carl.coach")
    private String username;

    @ApiModelProperty(value = "Trainer first name", example = "Carl")
    private String firstName;

    @ApiModelProperty(value = "Trainer last name", example = "Coach")
    private String lastName;

    @ApiModelProperty(value = "Trainer specialization (training type reference)")
    private TrainingTypeDto specialization;
}