package com.gym.crm.client;

import com.gym.crm.domain.Training;
import com.gym.crm.domain.User;
import com.gym.crm.dto.workload.WorkloadEventRequest;
import com.gym.crm.logging.LoggingConstants;
import com.gym.crm.security.InternalServiceTokenProvider;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WorkloadClientImpl implements WorkloadClient {

    private static final Logger log = LoggerFactory.getLogger(WorkloadClientImpl.class);
    private static final String WORKLOAD_PATH = "/api/trainer-workloads";

    private final RestClient restClient;
    private final InternalServiceTokenProvider internalServiceTokenProvider;

    public WorkloadClientImpl(RestClient workloadRestClient,
                              InternalServiceTokenProvider internalServiceTokenProvider) {
        this.restClient = workloadRestClient;
        this.internalServiceTokenProvider = internalServiceTokenProvider;
    }

    @Override
    @CircuitBreaker(name = "trainerWorkloadService", fallbackMethod = "onWorkloadNotificationFailure")
    public void notify(Training training, WorkloadActionType actionType) {
        WorkloadEventRequest request = buildRequest(training, actionType);

        restClient.post()
                .uri(WORKLOAD_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + internalServiceTokenProvider.generateToken())
                .header(LoggingConstants.TRANSACTION_ID_HEADER, MDC.get(LoggingConstants.TRANSACTION_ID_MDC_KEY))
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    void onWorkloadNotificationFailure(Training training, WorkloadActionType actionType, Throwable throwable) {
        log.warn("Workload notification failed and was skipped: transactionId={} trainingId={} actionType={} reason={}",
                MDC.get(LoggingConstants.TRANSACTION_ID_MDC_KEY), training.getId(), actionType, throwable.getMessage());
    }

    private WorkloadEventRequest buildRequest(Training training, WorkloadActionType actionType) {
        User trainerUser = training.getTrainer().getUser();

        WorkloadEventRequest request = new WorkloadEventRequest();
        request.setTrainerUsername(trainerUser.getUsername());
        request.setTrainerFirstName(trainerUser.getFirstName());
        request.setTrainerLastName(trainerUser.getLastName());
        request.setActive(trainerUser.isActive());
        request.setTrainingDate(training.getTrainingDate());
        request.setTrainingDuration(training.getTrainingDuration());
        request.setActionType(actionType);
        return request;
    }
}