package com.gym.crm.dto.trainer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gym.crm.dto.common.TraineeShortDto;
import com.gym.crm.dto.common.TrainingTypeDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrainerProfileResponse {

    @ApiModelProperty(value = "Username (present only in the update-profile response)", example = "carl.coach")
    private String username;

    @ApiModelProperty(value = "First name", example = "Carl")
    private String firstName;

    @ApiModelProperty(value = "Last name", example = "Coach")
    private String lastName;

    @ApiModelProperty(value = "Specialization (training type reference)")
    private TrainingTypeDto specialization;

    @ApiModelProperty(value = "Active status")
    private boolean active;

    @ApiModelProperty(value = "Assigned trainees")
    private List<TraineeShortDto> trainees;
}