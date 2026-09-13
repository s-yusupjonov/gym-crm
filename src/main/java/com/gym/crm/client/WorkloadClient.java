package com.gym.crm.client;

import com.gym.crm.domain.Training;

public interface WorkloadClient {

    void notify(Training training, WorkloadActionType actionType);
}