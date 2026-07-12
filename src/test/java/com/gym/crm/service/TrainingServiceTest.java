package com.gym.crm.service;
import com.gym.crm.dao.TrainingDao;
import com.gym.crm.domain.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {
    @Mock private TrainingDao trainingDao;
    private TrainingService service;
    @BeforeEach void setUp() { service = new TrainingService(trainingDao); }
    @Test void shouldCreateTraining() {
        Training training = new Training(null,1L,2L,"Session",TrainingType.CARDIO,LocalDate.now(),60);
        when(trainingDao.create(any(Training.class))).thenAnswer(i -> i.getArgument(0));
        Training r = service.create(training);
        assertEquals("Session", r.getTrainingName());
    }
    @Test void shouldSelectTraining() {
        Training t = new Training(); t.setTrainingId(1L);
        when(trainingDao.findById(1L)).thenReturn(Optional.of(t));
        assertTrue(service.select(1L).isPresent());
    }
}
