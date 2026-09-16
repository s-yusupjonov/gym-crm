package com.gym.crm.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.Training;
import com.gym.crm.domain.User;
import com.gym.crm.logging.LoggingConstants;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkloadClientImplTest {

    private static final String QUEUE_NAME = "workload.events.queue";

    @Mock
    private JmsTemplate jmsTemplate;

    private ObjectMapper objectMapper;
    private WorkloadClientImpl workloadClient;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        workloadClient = new WorkloadClientImpl(jmsTemplate, objectMapper, QUEUE_NAME);
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void notifyShouldSendMessageWithExpectedPayloadAndTransactionIdProperty() throws JMSException {
        MDC.put(LoggingConstants.TRANSACTION_ID_MDC_KEY, "txn-123");

        Session session = mock(Session.class);
        TextMessage textMessage = mock(TextMessage.class);
        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        when(session.createTextMessage(payloadCaptor.capture())).thenReturn(textMessage);

        ArgumentCaptor<MessageCreator> messageCreatorCaptor = ArgumentCaptor.forClass(MessageCreator.class);

        workloadClient.notify(validTraining(), WorkloadActionType.ADD);

        verify(jmsTemplate).send(eq(QUEUE_NAME), messageCreatorCaptor.capture());

        Message createdMessage = messageCreatorCaptor.getValue().createMessage(session);

        assertSame(textMessage, createdMessage);
        verify(textMessage).setStringProperty(WorkloadClientImpl.TRANSACTION_ID_PROPERTY, "txn-123");

        JsonNode payload = readPayload(payloadCaptor.getValue());
        assertEquals("carl.coach", payload.get("trainerUsername").asText());
        assertEquals("Carl", payload.get("trainerFirstName").asText());
        assertEquals("Coach", payload.get("trainerLastName").asText());
        assertTrue(payload.get("isActive").asBoolean());
        assertEquals("2026-08-01", payload.get("trainingDate").asText());
        assertEquals(60, payload.get("trainingDuration").asInt());
        assertEquals("ADD", payload.get("actionType").asText());
    }

    @Test
    void notifyShouldNotSetTransactionIdPropertyWhenMdcIsEmpty() throws JMSException {
        Session session = mock(Session.class);
        TextMessage textMessage = mock(TextMessage.class);
        when(session.createTextMessage(any(String.class))).thenReturn(textMessage);

        ArgumentCaptor<MessageCreator> messageCreatorCaptor = ArgumentCaptor.forClass(MessageCreator.class);

        workloadClient.notify(validTraining(), WorkloadActionType.DELETE);

        verify(jmsTemplate).send(eq(QUEUE_NAME), messageCreatorCaptor.capture());

        messageCreatorCaptor.getValue().createMessage(session);

        verifyNoMoreInteractions(textMessage);
    }

    @Test
    void notifyShouldPropagateExceptionWhenJmsSendFails() {
        doThrow(new JmsException("broker unavailable") {
        }).when(jmsTemplate).send(eq(QUEUE_NAME), any(MessageCreator.class));

        assertThrows(JmsException.class,
                () -> workloadClient.notify(validTraining(), WorkloadActionType.ADD));
    }

    @Test
    void buildMessageShouldPropagateJmsExceptionFromSession() throws JMSException {
        Session session = mock(Session.class);
        when(session.createTextMessage(any(String.class))).thenThrow(new JMSException("cannot create message"));

        ArgumentCaptor<MessageCreator> messageCreatorCaptor = ArgumentCaptor.forClass(MessageCreator.class);

        workloadClient.notify(validTraining(), WorkloadActionType.ADD);

        verify(jmsTemplate).send(eq(QUEUE_NAME), messageCreatorCaptor.capture());

        assertThrows(JMSException.class, () -> messageCreatorCaptor.getValue().createMessage(session));
    }

    private JsonNode readPayload(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
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