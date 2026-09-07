package com.gym.crm.service;

import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.TrainingType;
import com.gym.crm.domain.User;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.ValidationException;
import com.gym.crm.repository.TrainerRepository;
import com.gym.crm.repository.TrainingTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        trainerService = new TrainerService(trainerRepository, userProfileService, trainingTypeRepository);
    }

    private Trainer trainerWithUser(String firstName, String lastName, TrainingType specialization) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(specialization);
        return trainer;
    }

    @Test
    void createTrainerProfileShouldThrowWhenUserIsNull() {
        Trainer trainer = new Trainer();

        assertThrows(ValidationException.class, () -> trainerService.createTrainerProfile(trainer));
    }

    @Test
    void createTrainerProfileShouldThrowWhenFirstNameIsBlank() {
        Trainer trainer = trainerWithUser(" ", "Coach", new TrainingType());

        assertThrows(ValidationException.class, () -> trainerService.createTrainerProfile(trainer));
    }

    @Test
    void createTrainerProfileShouldThrowWhenLastNameIsBlank() {
        Trainer trainer = trainerWithUser("Carl", null, new TrainingType());

        assertThrows(ValidationException.class, () -> trainerService.createTrainerProfile(trainer));
    }

    @Test
    void createTrainerProfileShouldThrowWhenSpecializationIsNull() {
        Trainer trainer = trainerWithUser("Carl", "Coach", null);

        assertThrows(ValidationException.class, () -> trainerService.createTrainerProfile(trainer));
    }

    @Test
    void createTrainerProfileShouldGenerateUsernamePasswordAndSetActive() {
        Trainer trainer = trainerWithUser("Carl", "Coach", new TrainingType());
        when(userProfileService.generateUsername("Carl", "Coach")).thenReturn("Carl.Coach");
        when(userProfileService.generatePassword()).thenReturn("generatedPass");
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        Trainer saved = trainerService.createTrainerProfile(trainer);

        assertEquals("Carl.Coach", saved.getUser().getUsername());
        assertEquals("generatedPass", saved.getUser().getPassword());
        assertEquals(true, saved.getUser().isActive());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void updateTrainerProfileShouldThrowWhenUserIsNull() {
        Trainer updates = new Trainer();

        assertThrows(ValidationException.class,
                () -> trainerService.updateTrainerProfile("carl.coach", updates));
    }

    @Test
    void updateTrainerProfileShouldThrowWhenNamesAreBlank() {
        Trainer updates = trainerWithUser("", "Coach", new TrainingType());

        assertThrows(ValidationException.class,
                () -> trainerService.updateTrainerProfile("carl.coach", updates));
    }

    @Test
    void updateTrainerProfileShouldThrowWhenTrainerNotFound() {
        Trainer updates = trainerWithUser("Carl", "Coach", new TrainingType());
        when(trainerRepository.findByUsername("carl.coach")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> trainerService.updateTrainerProfile("carl.coach", updates));
    }

    @Test
    void updateTrainerProfileShouldUpdateExistingFields() {
        Trainer existing = trainerWithUser("Old", "Name", new TrainingType());
        TrainingType newSpecialization = new TrainingType();
        newSpecialization.setTrainingTypeName("YOGA");
        Trainer updates = trainerWithUser("New", "Name", newSpecialization);
        when(trainerRepository.findByUsername("carl.coach")).thenReturn(Optional.of(existing));

        Trainer result = trainerService.updateTrainerProfile("carl.coach", updates);

        assertEquals("New", result.getUser().getFirstName());
        assertEquals(newSpecialization, result.getSpecialization());
    }

    @Test
    void changePasswordShouldThrowWhenPasswordIsNull() {
        assertThrows(ValidationException.class,
                () -> trainerService.changePassword("carl.coach", null));
    }

    @Test
    void changePasswordShouldThrowWhenPasswordIsBlank() {
        assertThrows(ValidationException.class,
                () -> trainerService.changePassword("carl.coach", ""));
    }

    @Test
    void changePasswordShouldUpdatePasswordWhenValid() {
        Trainer existing = trainerWithUser("Carl", "Coach", new TrainingType());
        when(trainerRepository.findByUsername("carl.coach")).thenReturn(Optional.of(existing));

        trainerService.changePassword("carl.coach", "newPass");

        assertEquals("newPass", existing.getUser().getPassword());
    }

    @Test
    void setActiveShouldUpdateActiveFlag() {
        Trainer existing = trainerWithUser("Carl", "Coach", new TrainingType());
        when(trainerRepository.findByUsername("carl.coach")).thenReturn(Optional.of(existing));

        trainerService.setActive("carl.coach", false);

        assertEquals(false, existing.getUser().isActive());
    }

    @Test
    void getByUsernameShouldThrowWhenNotFound() {
        when(trainerRepository.findByUsername("nobody")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> trainerService.getByUsername("nobody"));
    }

    @Test
    void getByUsernameShouldReturnTrainerWhenFound() {
        Trainer existing = trainerWithUser("Carl", "Coach", new TrainingType());
        when(trainerRepository.findByUsername("carl.coach")).thenReturn(Optional.of(existing));

        assertEquals(existing, trainerService.getByUsername("carl.coach"));
    }

    @Test
    void getAllShouldDelegateToRepository() {
        List<Trainer> all = List.of(trainerWithUser("Carl", "Coach", new TrainingType()));
        when(trainerRepository.findAll()).thenReturn(all);

        assertEquals(all, trainerService.getAll());
    }

    @Test
    void getTrainersNotAssignedToTraineeShouldDelegateToRepository() {
        List<Trainer> unassigned = List.of(trainerWithUser("Carl", "Coach", new TrainingType()));
        when(trainerRepository.findNotAssignedToTrainee("john.doe")).thenReturn(unassigned);

        assertEquals(unassigned, trainerService.getTrainersNotAssignedToTrainee("john.doe"));
    }
}