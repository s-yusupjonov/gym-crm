package com.gym.crm.health;

import com.gym.crm.dao.TrainingTypeDao;
import com.gym.crm.domain.TrainingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypeReferenceDataHealthIndicatorTest {

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @Test
    void healthShouldReportUpWhenTrainingTypesAreSeeded() {
        when(trainingTypeDao.findAll()).thenReturn(List.of(new TrainingType(), new TrainingType()));

        Health health = new TrainingTypeReferenceDataHealthIndicator(trainingTypeDao).health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(2, health.getDetails().get("trainingTypes"));
    }

    @Test
    void healthShouldReportDownWhenNoTrainingTypesAreSeeded() {
        when(trainingTypeDao.findAll()).thenReturn(Collections.emptyList());

        Health health = new TrainingTypeReferenceDataHealthIndicator(trainingTypeDao).health();

        assertEquals(Status.DOWN, health.getStatus());
    }
}