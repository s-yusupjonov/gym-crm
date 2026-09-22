package com.gym.crm.client;

import com.gym.crm.domain.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("standalone")
public class NoOpWorkloadClient implements WorkloadClient {

    private static final Logger log = LoggerFactory.getLogger(NoOpWorkloadClient.class);

    @Override
    public void notify(Training training, WorkloadActionType actionType) {
        log.info("[standalone] Workload integration disabled - skipping event: trainingId={} actionType={}",
                training.getId(), actionType);
    }
}
