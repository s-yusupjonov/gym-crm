package com.gym.crm.client;

import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.Training;
import com.gym.crm.domain.User;
import com.gym.crm.logging.LoggingConstants;
import com.gym.crm.security.InternalServiceTokenProvider;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class WorkloadClientImplTest {

    private static final String SECRET = "unit-test-internal-secret-key-that-is-long-enough-for-hs256";

    private MockRestServiceServer mockServer;
    private WorkloadClientImpl workloadClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://trainer-workload-service");
        mockServer = MockRestServiceServer.bindTo(builder).build();

        InternalServiceTokenProvider tokenProvider = new InternalServiceTokenProvider(SECRET, 60_000L);
        RestClient restClient = builder.build();
        workloadClient = new WorkloadClientImpl(restClient, tokenProvider);

        MDC.put(LoggingConstants.TRANSACTION_ID_MDC_KEY, "txn-123");
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void notifyShouldPostExpectedBodyAndHeaders() {
        mockServer.expect(requestTo("http://trainer-workload-service/api/trainer-workloads"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, Matchers.startsWith("Bearer ")))
                .andExpect(header(LoggingConstants.TRANSACTION_ID_HEADER, "txn-123"))
                .andExpect(jsonPath("$.trainerUsername").value("carl.coach"))
                .andExpect(jsonPath("$.trainerFirstName").value("Carl"))
                .andExpect(jsonPath("$.trainerLastName").value("Coach"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.trainingDate").value("2026-08-01"))
                .andExpect(jsonPath("$.trainingDuration").value(60))
                .andExpect(jsonPath("$.actionType").value("ADD"))
                .andRespond(withSuccess());

        workloadClient.notify(validTraining(), WorkloadActionType.ADD);

        mockServer.verify();
    }

    @Test
    void notifyShouldPropagateServerErrorWhenCircuitBreakerIsNotEngaged() {
        mockServer.expect(requestTo("http://trainer-workload-service/api/trainer-workloads"))
                .andRespond(withServerError());

        assertThrows(HttpServerErrorException.class,
                () -> workloadClient.notify(validTraining(), WorkloadActionType.DELETE));
    }

    @Test
    void fallbackShouldSwallowFailureAndNotThrow() {
        Training training = validTraining();
        training.setId(42L);

        assertDoesNotThrow(() -> workloadClient.onWorkloadNotificationFailure(
                training, WorkloadActionType.ADD, new TimeoutException("call timed out")));
    }

    private Training validTraining() {
        User user = new User();
        user.setUsername("carl.coach");
        user.setFirstName("Carl");
        user.setLastName("Coach");
        user.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        Training training = new Training();
        training.setTrainer(trainer);
        training.setTrainingDate(LocalDate.of(2026, 8, 1));
        training.setTrainingDuration(60);
        return training;
    }
}