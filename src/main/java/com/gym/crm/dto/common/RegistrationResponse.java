package com.gym.crm.dto.common;

import io.swagger.annotations.ApiModelProperty;

public class RegistrationResponse {

    @ApiModelProperty(value = "Generated username", example = "john.doe")
    private String username;

    @ApiModelProperty(value = "Generated password")
    private String password;

    public RegistrationResponse() {
    }

    public RegistrationResponse(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}