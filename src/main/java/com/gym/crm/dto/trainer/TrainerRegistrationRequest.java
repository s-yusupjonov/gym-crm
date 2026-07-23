package com.gym.crm.dto.trainer;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getSpecializationId() {
        return specializationId;
    }

    public void setSpecializationId(Long specializationId) {
        this.specializationId = specializationId;
    }
}