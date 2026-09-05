package com.gym.crm.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GymCrmMetricsTest {

    private MeterRegistry meterRegistry;
    private GymCrmMetrics metrics;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        metrics = new GymCrmMetrics(meterRegistry);
    }

    @Test
    void recordAuthenticationSuccessShouldIncrementSuccessCounterOnly() {
        metrics.recordAuthenticationSuccess();

        assertEquals(1.0, meterRegistry.get("gym_crm.authentication.attempts").tag("result", "success")
                .counter().count());
        assertEquals(0.0, meterRegistry.get("gym_crm.authentication.attempts").tag("result", "failure")
                .counter().count());
    }

    @Test
    void recordAuthenticationFailureShouldIncrementFailureCounterOnly() {
        metrics.recordAuthenticationFailure();

        assertEquals(1.0, meterRegistry.get("gym_crm.authentication.attempts").tag("result", "failure")
                .counter().count());
        assertEquals(0.0, meterRegistry.get("gym_crm.authentication.attempts").tag("result", "success")
                .counter().count());
    }

    @Test
    void recordTrainingCreatedShouldIncrementTrainingsCreatedCounter() {
        metrics.recordTrainingCreated();
        metrics.recordTrainingCreated();

        assertEquals(2.0, meterRegistry.get("gym_crm.trainings.created").counter().count());
    }
}