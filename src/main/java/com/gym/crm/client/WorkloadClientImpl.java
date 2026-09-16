package com.gym.crm.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.domain.Training;
import com.gym.crm.domain.User;
import com.gym.crm.dto.workload.WorkloadEventRequest;
import com.gym.crm.logging.LoggingConstants;
import jakarta.jms.JMSException;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class WorkloadClientImpl implements WorkloadClient {

    private static final Logger log = LoggerFactory.getLogger(WorkloadClientImpl.class);

    static final String TRANSACTION_ID_PROPERTY = "transactionId";

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    private final String workloadEventsQueue;

    public WorkloadClientImpl(JmsTemplate jmsTemplate,
                              ObjectMapper objectMapper,
                              @Value("${activemq.queue.workload-events}") String workloadEventsQueue) {
        this.jmsTemplate = jmsTemplate;
        this.objectMapper = objectMapper;
        this.workloadEventsQueue = workloadEventsQueue;
    }

    @Override
    public void notify(Training training, WorkloadActionType actionType) {
        WorkloadEventRequest request = buildRequest(training, actionType);
        String transactionId = MDC.get(LoggingConstants.TRANSACTION_ID_MDC_KEY);
        String payload = toJson(request);

        jmsTemplate.send(workloadEventsQueue, session -> buildMessage(session, payload, transactionId));

        log.info("Published workload event: transactionId={} queue={} trainingId={} actionType={}",
                transactionId, workloadEventsQueue, training.getId(), actionType);
    }

    private TextMessage buildMessage(Session session, String payload, String transactionId) throws JMSException {
        TextMessage message = session.createTextMessage(payload);
        if (transactionId != null) {
            message.setStringProperty(TRANSACTION_ID_PROPERTY, transactionId);
        }
        return message;
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

    private String toJson(WorkloadEventRequest request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize workload event", ex);
        }
    }
}
