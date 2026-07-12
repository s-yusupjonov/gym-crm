package com.gym.crm.service;
import com.gym.crm.dao.TraineeDao;
import com.gym.crm.domain.Trainee;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {
    @Mock private TraineeDao traineeDao;
    @Mock private UserProfileService userProfileService;
    private TraineeService service;
    @BeforeEach void setUp() {
        service = new TraineeService(traineeDao);
        service.setUserProfileService(userProfileService);
    }
    @Test void shouldCreateTraineeWithGeneratedCredentials() {
        Trainee input = new Trainee(); input.setFirstName("John"); input.setLastName("Smith");
        when(userProfileService.generateUsername("John","Smith")).thenReturn("John.Smith");
        when(userProfileService.generatePassword()).thenReturn("1234567890");
        when(traineeDao.create(any(Trainee.class))).thenAnswer(i -> i.getArgument(0));
        Trainee r = service.create(input);
        assertEquals("John.Smith", r.getUsername());
        assertEquals("1234567890", r.getPassword());
        assertTrue(r.isActive());
    }
    @Test void shouldSelectTrainee() {
        Trainee t = new Trainee(); t.setTraineeId(5L);
        when(traineeDao.findById(5L)).thenReturn(Optional.of(t));
        assertTrue(service.select(5L).isPresent());
    }
    @Test void shouldDeleteTrainee() { service.delete(9L); verify(traineeDao).delete(9L); }
    @Test void shouldUpdateTrainee() {
        Trainee t = new Trainee(); t.setTraineeId(2L);
        when(traineeDao.update(t)).thenReturn(t);
        assertEquals(2L, service.update(t).getTraineeId());
    }
}
