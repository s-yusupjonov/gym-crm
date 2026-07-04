package com.gym.crm.service;
import com.gym.crm.dao.*;
import com.gym.crm.domain.Trainee;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {
    @Mock private TraineeDao traineeDao;
    @Mock private TrainerDao trainerDao;
    private UserProfileService service;
    @BeforeEach void setUp() {
        service = new UserProfileService();
        service.setTraineeDao(traineeDao);
        service.setTrainerDao(trainerDao);
    }
    @Test void shouldGenerateSimpleUsernameWhenNoDuplicates() {
        when(traineeDao.findAll()).thenReturn(List.of());
        when(trainerDao.findAll()).thenReturn(List.of());
        assertEquals("John.Smith", service.generateUsername("John", "Smith"));
    }
    @Test void shouldAppendSerialNumberOnDuplicate() {
        Trainee e = new Trainee(); e.setUsername("John.Smith");
        when(traineeDao.findAll()).thenReturn(List.of(e));
        when(trainerDao.findAll()).thenReturn(List.of());
        assertEquals("John.Smith1", service.generateUsername("John", "Smith"));
    }
    @Test void shouldGeneratePasswordOfLength10() {
        String pwd = service.generatePassword();
        assertNotNull(pwd); assertEquals(10, pwd.length());
    }
}
