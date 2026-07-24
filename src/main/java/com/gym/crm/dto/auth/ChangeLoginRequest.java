package com.gym.crm.dto.auth;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}