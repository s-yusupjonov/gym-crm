package com.gym.crm.service;

import com.gym.crm.domain.Trainee;
import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.User;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.ValidationException;
import com.gym.crm.repository.TraineeRepository;
import com.gym.crm.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserProfileService userProfileService;

    private TraineeService traineeService;

    @BeforeEach
    void setUp() {
        traineeService = new TraineeService(traineeRepository, trainerRepository, userProfileService);
    }

    private Trainee traineeWithUser(String firstName, String lastName) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        Trainee trainee = new Trainee();
        trainee.setUser(user);
        return trainee;
    }

    @Test
    void createTraineeProfileShouldThrowWhenUserIsNull() {
        Trainee trainee = new Trainee();

        assertThrows(ValidationException.class, () -> traineeService.createTraineeProfile(trainee));
    }

    @Test
    void createTraineeProfileShouldThrowWhenFirstNameIsBlank() {
        Trainee trainee = traineeWithUser("  ", "Doe");

        assertThrows(ValidationException.class, () -> traineeService.createTraineeProfile(trainee));
    }

    @Test
    void createTraineeProfileShouldThrowWhenLastNameIsBlank() {
        Trainee trainee = traineeWithUser("John", null);

        assertThrows(ValidationException.class, () -> traineeService.createTraineeProfile(trainee));
    }

    @Test
    void createTraineeProfileShouldGenerateUsernamePasswordAndSetActive() {
        Trainee trainee = traineeWithUser("John", "Doe");
        when(userProfileService.generateUsername("John", "Doe")).thenReturn("John.Doe");
        when(userProfileService.generatePassword()).thenReturn("generatedPass");
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        Trainee saved = traineeService.createTraineeProfile(trainee);

        assertEquals("John.Doe", saved.getUser().getUsername());
        assertEquals("generatedPass", saved.getUser().getPassword());
        assertTrue(saved.getUser().isActive());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void updateTraineeProfileShouldThrowWhenUserIsNull() {
        Trainee updates = new Trainee();

        assertThrows(ValidationException.class,
                () -> traineeService.updateTraineeProfile("john.doe", updates));
    }

    @Test
    void updateTraineeProfileShouldThrowWhenNamesAreBlank() {
        Trainee updates = traineeWithUser("", "Doe");

        assertThrows(ValidationException.class,
                () -> traineeService.updateTraineeProfile("john.doe", updates));
    }

    @Test
    void updateTraineeProfileShouldThrowWhenTraineeNotFound() {
        Trainee updates = traineeWithUser("John", "Doe");
        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> traineeService.updateTraineeProfile("john.doe", updates));
    }

    @Test
    void updateTraineeProfileShouldUpdateExistingFields() {
        Trainee existing = traineeWithUser("Old", "Name");
        Trainee updates = traineeWithUser("New", "Name");
        updates.setAddress("New Address");
        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(existing));

        Trainee result = traineeService.updateTraineeProfile("john.doe", updates);

        assertEquals("New", result.getUser().getFirstName());
        assertEquals("New Address", result.getAddress());
    }

    @Test
    void changePasswordShouldThrowWhenPasswordIsNull() {
        assertThrows(ValidationException.class,
                () -> traineeService.changePassword("john.doe", null));
    }

    @Test
    void changePasswordShouldThrowWhenPasswordIsBlank() {
        assertThrows(ValidationException.class,
                () -> traineeService.changePassword("john.doe", "   "));
    }

    @Test
    void changePasswordShouldUpdatePasswordWhenValid() {
        Trainee existing = traineeWithUser("John", "Doe");
        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(existing));

        traineeService.changePassword("john.doe", "newPass");

        assertEquals("newPass", existing.getUser().getPassword());
    }

    @Test
    void setActiveShouldUpdateActiveFlag() {
        Trainee existing = traineeWithUser("John", "Doe");
        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(existing));

        traineeService.setActive("john.doe", false);

        assertEquals(false, existing.getUser().isActive());
    }

    @Test
    void deleteByUsernameShouldDeleteFoundTrainee() {
        Trainee existing = traineeWithUser("John", "Doe");
        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(existing));

        traineeService.deleteByUsername("john.doe");

        verify(traineeRepository).delete(existing);
    }

    @Test
    void getByUsernameShouldThrowWhenNotFound() {
        when(traineeRepository.findByUsername("nobody")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> traineeService.getByUsername("nobody"));
    }

    @Test
    void getByUsernameShouldReturnTraineeWhenFound() {
        Trainee existing = traineeWithUser("John", "Doe");
        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(existing));

        assertEquals(existing, traineeService.getByUsername("john.doe"));
    }

    @Test
    void getAllShouldDelegateToRepository() {
        List<Trainee> all = List.of(traineeWithUser("John", "Doe"));
        when(traineeRepository.findAll()).thenReturn(all);

        assertEquals(all, traineeService.getAll());
    }

    @Test
    void updateTrainersListShouldThrowWhenSomeUsernamesDoNotExist() {
        Trainee existing = traineeWithUser("John", "Doe");
        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(existing));
        when(trainerRepository.findByUsernames(List.of("t1", "t2"))).thenReturn(List.of(new Trainer()));

        assertThrows(ValidationException.class,
                () -> traineeService.updateTrainersList("john.doe", List.of("t1", "t2")));
    }

    @Test
    void updateTrainersListShouldSetTrainersWhenAllExist() {
        Trainee existing = traineeWithUser("John", "Doe");
        Trainer trainer1 = new Trainer();
        Trainer trainer2 = new Trainer();
        when(traineeRepository.findByUsername("john.doe")).thenReturn(Optional.of(existing));
        when(trainerRepository.findByUsernames(List.of("t1", "t2"))).thenReturn(List.of(trainer1, trainer2));

        List<Trainer> result = traineeService.updateTrainersList("john.doe", List.of("t1", "t2"));

        assertEquals(2, result.size());
        assertEquals(2, existing.getTrainers().size());
    }
}