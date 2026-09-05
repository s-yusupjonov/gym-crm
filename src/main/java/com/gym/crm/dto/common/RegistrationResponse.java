package com.gym.crm.dto.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationResponse {

    @ApiModelProperty(value = "Generated username", example = "john.doe")
    private String username;

    @ApiModelProperty(value = "Generated password")
    private String password;
}