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
public class TraineeShortDto {

    @ApiModelProperty(value = "Trainee username", example = "john.doe")
    private String username;

    @ApiModelProperty(value = "Trainee first name", example = "John")
    private String firstName;

    @ApiModelProperty(value = "Trainee last name", example = "Doe")
    private String lastName;
}