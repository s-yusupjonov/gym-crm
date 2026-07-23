package com.gym.crm.dto.auth;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;

public class ChangeLoginRequest {

    @NotBlank(message = "Username is required")
    @ApiModelProperty(value = "Username of the trainee/trainer", required = true, example = "john.doe")
    private String username;

    @NotBlank(message = "Old password is required")
    @ApiModelProperty(value = "Current password", required = true)
    private String oldPassword;

    @NotBlank(message = "New password is required")
    @ApiModelProperty(value = "New password to set", required = true)
    private String newPassword;

    public ChangeLoginRequest() {
    }

    public ChangeLoginRequest(String username, String oldPassword, String newPassword) {
        this.username = username;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}