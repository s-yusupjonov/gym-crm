package com.gym.crm.dto.common;

import io.swagger.annotations.ApiModelProperty;

public class TrainingTypeDto {

    @ApiModelProperty(value = "Training type id", example = "1")
    private Long id;

    @ApiModelProperty(value = "Training type name", example = "CARDIO")
    private String trainingTypeName;

    public TrainingTypeDto() {
    }

    public TrainingTypeDto(Long id, String trainingTypeName) {
        this.id = id;
        this.trainingTypeName = trainingTypeName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrainingTypeName() {
        return trainingTypeName;
    }

    public void setTrainingTypeName(String trainingTypeName) {
        this.trainingTypeName = trainingTypeName;
    }
}