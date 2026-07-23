package com.gym.crm.dto.trainer;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateTrainerRequest {

    @NotBlank(message = "First name is required")
    @ApiModelProperty(value = "First name", required = true, example = "Carl")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @ApiModelProperty(value = "Last name", required = true, example = "Coach")
    private String lastName;

    @NotNull(message = "isActive is required")
    @ApiModelProperty(value = "Active status", required = true, example = "true")
    private Boolean active;

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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}