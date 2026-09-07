package com.gym.crm.health;

import com.gym.crm.repository.TrainingTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypeReferenceDataHealthIndicatorTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Test
    void healthShouldReportUpWhenTrainingTypesAreSeeded() {
        when(trainingTypeRepository.count()).thenReturn(2L);

        Health health = new TrainingTypeReferenceDataHealthIndicator(trainingTypeRepository).health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(2L, health.getDetails().get("trainingTypes"));
    }

    @Test
    void healthShouldReportDownWhenNoTrainingTypesAreSeeded() {
        when(trainingTypeRepository.count()).thenReturn(0L);

        Health health = new TrainingTypeReferenceDataHealthIndicator(trainingTypeRepository).health();

        assertEquals(Status.DOWN, health.getStatus());
    }
}