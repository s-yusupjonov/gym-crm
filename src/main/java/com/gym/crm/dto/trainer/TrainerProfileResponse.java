package com.gym.crm.dto.trainer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gym.crm.dto.common.TraineeShortDto;
import com.gym.crm.dto.common.TrainingTypeDto;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

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

    public TrainingTypeDto getSpecialization() {
        return specialization;
    }

    public void setSpecialization(TrainingTypeDto specialization) {
        this.specialization = specialization;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<TraineeShortDto> getTrainees() {
        return trainees;
    }

    public void setTrainees(List<TraineeShortDto> trainees) {
        this.trainees = trainees;
    }
}