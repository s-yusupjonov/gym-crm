package com.gym.crm.dto.trainer;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TrainerRegistrationRequest {

    @NotBlank(message = "First name is required")
    @ApiModelProperty(value = "First name", required = true, example = "Carl")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @ApiModelProperty(value = "Last name", required = true, example = "Coach")
    private String lastName;

    @NotNull(message = "Specialization is required")
    @ApiModelProperty(value = "Specialization - training type id (training type reference)", required = true,
            example = "1")
    private Long specializationId;
}