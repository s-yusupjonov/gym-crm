package com.gym.crm.dto.trainee;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gym.crm.dto.common.TrainerShortDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TraineeProfileResponse {

    @ApiModelProperty(value = "Username (present only in the update-profile response)", example = "john.doe")
    private String username;

    @ApiModelProperty(value = "First name", example = "John")
    private String firstName;

    @ApiModelProperty(value = "Last name", example = "Doe")
    private String lastName;

    @ApiModelProperty(value = "Date of birth")
    private LocalDate dateOfBirth;

    @ApiModelProperty(value = "Address")
    private String address;

    @ApiModelProperty(value = "Active status")
    private boolean active;

    @ApiModelProperty(value = "Assigned trainers")
    private List<TrainerShortDto> trainers;
}