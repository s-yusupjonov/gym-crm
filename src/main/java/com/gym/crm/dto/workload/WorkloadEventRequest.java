package com.gym.crm.dto.workload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gym.crm.client.WorkloadActionType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class WorkloadEventRequest {

    private String trainerUsername;

    private String trainerFirstName;

    private String trainerLastName;

    @JsonProperty("isActive")
    private boolean active;

    private LocalDate trainingDate;

    private int trainingDuration;

    private WorkloadActionType actionType;
}