package com.gym.crm.init;

import com.gym.crm.domain.TrainingType;
import com.gym.crm.repository.TrainingTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypeSeederTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Test
    void seedShouldPersistDefaultTypesWhenTableIsEmpty() {
        when(trainingTypeRepository.count()).thenReturn(0L);

        new TrainingTypeSeeder(trainingTypeRepository).seed();

        ArgumentCaptor<List<TrainingType>> captor = ArgumentCaptor.forClass(List.class);
        verify(trainingTypeRepository).saveAll(captor.capture());
        assertEquals(6, captor.getValue().size());
    }

    @Test
    void seedShouldNotPersistWhenTypesAlreadyExist() {
        when(trainingTypeRepository.count()).thenReturn(3L);

        new TrainingTypeSeeder(trainingTypeRepository).seed();

        verify(trainingTypeRepository, never()).saveAll(anyList());
    }
}