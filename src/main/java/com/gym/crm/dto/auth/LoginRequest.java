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
public class LoginRequest {

    @NotBlank(message = "Username is required")
    @ApiModelProperty(value = "Username of the trainee/trainer", required = true, example = "john.doe")
    private String username;

    @NotBlank(message = "Password is required")
    @ApiModelProperty(value = "Password", required = true)
    private String password;
}