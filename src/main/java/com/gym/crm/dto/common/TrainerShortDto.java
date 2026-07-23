package com.gym.crm.dto.common;

import io.swagger.annotations.ApiModelProperty;

public class TrainerShortDto {

    @ApiModelProperty(value = "Trainer username", example = "carl.coach")
    private String username;

    @ApiModelProperty(value = "Trainer first name", example = "Carl")
    private String firstName;

    @ApiModelProperty(value = "Trainer last name", example = "Coach")
    private String lastName;

    @ApiModelProperty(value = "Trainer specialization (training type reference)")
    private TrainingTypeDto specialization;

    public TrainerShortDto() {
    }

    public TrainerShortDto(String username, String firstName, String lastName, TrainingTypeDto specialization) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
    }

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
}