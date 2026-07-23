package com.gym.crm.dto.common;

import io.swagger.annotations.ApiModelProperty;

public class TraineeShortDto {

    @ApiModelProperty(value = "Trainee username", example = "john.doe")
    private String username;

    @ApiModelProperty(value = "Trainee first name", example = "John")
    private String firstName;

    @ApiModelProperty(value = "Trainee last name", example = "Doe")
    private String lastName;

    public TraineeShortDto() {
    }

    public TraineeShortDto(String username, String firstName, String lastName) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
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
}