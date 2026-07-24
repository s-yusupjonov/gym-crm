package com.gym.crm.dto.common;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActiveStatusRequest {

    @NotNull(message = "isActive is required")
    @ApiModelProperty(value = "Active status", required = true, example = "true")
    private Boolean active;
}