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
public class TrainingTypeDto {

    @ApiModelProperty(value = "Training type id", example = "1")
    private Long id;

    @ApiModelProperty(value = "Training type name", example = "CARDIO")
    private String trainingTypeName;
}