package com.gym.crm.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class GymCrmMetrics {

    private static final String RESULT_TAG = "result";

    private final Counter authenticationSuccessCounter;
    private final Counter authenticationFailureCounter;
    private final Counter trainingsCreatedCounter;
    private final Counter trainingsCancelledCounter;

    public GymCrmMetrics(MeterRegistry meterRegistry) {
        this.authenticationSuccessCounter = Counter.builder("gym_crm.authentication.attempts")
                .description("Number of authentication attempts")
                .tag(RESULT_TAG, "success")
                .register(meterRegistry);
        this.authenticationFailureCounter = Counter.builder("gym_crm.authentication.attempts")
                .description("Number of authentication attempts")
                .tag(RESULT_TAG, "failure")
                .register(meterRegistry);
        this.trainingsCreatedCounter = Counter.builder("gym_crm.trainings.created")
                .description("Number of trainings created")
                .register(meterRegistry);
        this.trainingsCancelledCounter = Counter.builder("gym_crm.trainings.cancelled")
                .description("Number of trainings cancelled")
                .register(meterRegistry);
    }

    public void recordAuthenticationSuccess() {
        authenticationSuccessCounter.increment();
    }

    public void recordAuthenticationFailure() {
        authenticationFailureCounter.increment();
    }

    public void recordTrainingCreated() {
        trainingsCreatedCounter.increment();
    }

    public void recordTrainingCancelled() {
        trainingsCancelledCounter.increment();
    }
}