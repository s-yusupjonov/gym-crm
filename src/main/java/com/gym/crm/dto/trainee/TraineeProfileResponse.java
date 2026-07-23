package com.gym.crm.dto.trainee;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gym.crm.dto.common.TrainerShortDto;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDate;
import java.util.List;

/**
 * Response body for both "Get Trainee Profile" and "Update Trainee Profile".
 * Per the spec, only the update response includes the username - {@code username}
 * is left null (and therefore omitted from the JSON) for the get-profile case.
 */
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<TrainerShortDto> getTrainers() {
        return trainers;
    }

    public void setTrainers(List<TrainerShortDto> trainers) {
        this.trainers = trainers;
    }
}