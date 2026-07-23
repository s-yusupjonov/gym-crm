package com.gym.crm.dto.trainee;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class TraineeRegistrationRequest {

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
}