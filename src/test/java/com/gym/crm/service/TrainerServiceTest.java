package com.gym.crm.service;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.domain.Trainer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {
    @Mock private TrainerDao trainerDao;
    @Mock private UserProfileService userProfileService;
    private TrainerService service;
    @BeforeEach void setUp() {
        service = new TrainerService(trainerDao);
        service.setUserProfileService(userProfileService);
    }
    @Test void shouldCreateTrainerWithGeneratedCredentials() {
        Trainer input = new Trainer(); input.setFirstName("Anna"); input.setLastName("Jones");
        when(userProfileService.generateUsername("Anna","Jones")).thenReturn("Anna.Jones");
        when(userProfileService.generatePassword()).thenReturn("pass123456");
        when(trainerDao.create(any(Trainer.class))).thenAnswer(i -> i.getArgument(0));
        Trainer r = service.create(input);
        assertEquals("Anna.Jones", r.getUsername());
        assertTrue(r.isActive());
    }
    @Test void shouldSelectTrainer() {
        Trainer t = new Trainer(); t.setTrainerId(3L);
        when(trainerDao.findById(3L)).thenReturn(Optional.of(t));
        assertTrue(service.select(3L).isPresent());
    }
}
