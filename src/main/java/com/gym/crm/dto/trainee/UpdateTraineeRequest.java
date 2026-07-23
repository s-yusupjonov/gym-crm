package com.gym.crm.dto.trainee;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class UpdateTraineeRequest {

    @NotBlank(message = "First name is required")
    @ApiModelProperty(value = "First name", required = true, example = "John")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @ApiModelProperty(value = "Last name", required = true, example = "Doe")
    private String lastName;

    @ApiModelProperty(value = "Date of birth (optional)", example = "1995-05-20")
    private LocalDate dateOfBirth;

    @ApiModelProperty(value = "Address (optional)", example = "123 Main St")
    private String address;

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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}