package com.gym.crm.facade;

import com.gym.crm.domain.Trainee;
import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.Training;
import com.gym.crm.service.AuthenticationService;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @Mock
    private AuthenticationService authenticationService;

    private GymFacade gymFacade;

    @BeforeEach
    void setUp() {
        gymFacade = new GymFacade(traineeService, trainerService, trainingService, authenticationService);
    }

    @Test
    void createTraineeProfileShouldNotAuthenticateAndShouldDelegate() {
        Trainee trainee = new Trainee();
        when(traineeService.createTraineeProfile(trainee)).thenReturn(trainee);

        Trainee result = gymFacade.createTraineeProfile(trainee);

        assertEquals(trainee, result);
        verifyNoInteractions(authenticationService);
    }

    @Test
    void createTrainerProfileShouldNotAuthenticateAndShouldDelegate() {
        Trainer trainer = new Trainer();
        when(trainerService.createTrainerProfile(trainer)).thenReturn(trainer);

        Trainer result = gymFacade.createTrainerProfile(trainer);

        assertEquals(trainer, result);
        verifyNoInteractions(authenticationService);
    }

    @Test
    void matchTraineeCredentialsShouldDelegateWithoutThrowingAuthentication() {
        when(authenticationService.matchCredentials("john.doe", "pass")).thenReturn(true);

        assertTrue(gymFacade.matchTraineeCredentials("john.doe", "pass"));
    }

    @Test
    void matchTrainerCredentialsShouldDelegate() {
        when(authenticationService.matchCredentials("carl.coach", "pass")).thenReturn(true);

        assertTrue(gymFacade.matchTrainerCredentials("carl.coach", "pass"));
    }

    @Test
    void getTraineeProfileShouldAuthenticateBeforeFetching() {
        Trainee trainee = new Trainee();
        when(traineeService.getByUsername("john.doe")).thenReturn(trainee);

        Trainee result = gymFacade.getTraineeProfile("john.doe", "pass");

        assertEquals(trainee, result);
        InOrder order = inOrder(authenticationService, traineeService);
        order.verify(authenticationService).authenticate("john.doe", "pass");
        order.verify(traineeService).getByUsername("john.doe");
    }

    @Test
    void getTrainerProfileShouldAuthenticateBeforeFetching() {
        Trainer trainer = new Trainer();
        when(trainerService.getByUsername("carl.coach")).thenReturn(trainer);

        Trainer result = gymFacade.getTrainerProfile("carl.coach", "pass");

        assertEquals(trainer, result);
        InOrder order = inOrder(authenticationService, trainerService);
        order.verify(authenticationService).authenticate("carl.coach", "pass");
        order.verify(trainerService).getByUsername("carl.coach");
    }

    @Test
    void updateTraineeProfileShouldAuthenticateBeforeUpdating() {
        Trainee updates = new Trainee();
        Trainee updated = new Trainee();
        when(traineeService.updateTraineeProfile("john.doe", updates)).thenReturn(updated);

        Trainee result = gymFacade.updateTraineeProfile("john.doe", "pass", updates);

        assertEquals(updated, result);
        InOrder order = inOrder(authenticationService, traineeService);
        order.verify(authenticationService).authenticate("john.doe", "pass");
        order.verify(traineeService).updateTraineeProfile("john.doe", updates);
    }

    @Test
    void updateTrainerProfileShouldAuthenticateBeforeUpdating() {
        Trainer updates = new Trainer();
        Trainer updated = new Trainer();
        when(trainerService.updateTrainerProfile("carl.coach", updates)).thenReturn(updated);

        Trainer result = gymFacade.updateTrainerProfile("carl.coach", "pass", updates);

        assertEquals(updated, result);
        InOrder order = inOrder(authenticationService, trainerService);
        order.verify(authenticationService).authenticate("carl.coach", "pass");
        order.verify(trainerService).updateTrainerProfile("carl.coach", updates);
    }

    @Test
    void changeTraineePasswordShouldAuthenticateBeforeChanging() {
        gymFacade.changeTraineePassword("john.doe", "oldPass", "newPass");

        InOrder order = inOrder(authenticationService, traineeService);
        order.verify(authenticationService).authenticate("john.doe", "oldPass");
        order.verify(traineeService).changePassword("john.doe", "newPass");
    }

    @Test
    void changeTrainerPasswordShouldAuthenticateBeforeChanging() {
        gymFacade.changeTrainerPassword("carl.coach", "oldPass", "newPass");

        InOrder order = inOrder(authenticationService, trainerService);
        order.verify(authenticationService).authenticate("carl.coach", "oldPass");
        order.verify(trainerService).changePassword("carl.coach", "newPass");
    }

    @Test
    void activateDeactivateTraineeShouldAuthenticateBeforeSettingActive() {
        gymFacade.activateDeactivateTrainee("john.doe", "pass", false);

        InOrder order = inOrder(authenticationService, traineeService);
        order.verify(authenticationService).authenticate("john.doe", "pass");
        order.verify(traineeService).setActive("john.doe", false);
    }

    @Test
    void activateDeactivateTrainerShouldAuthenticateBeforeSettingActive() {
        gymFacade.activateDeactivateTrainer("carl.coach", "pass", true);

        InOrder order = inOrder(authenticationService, trainerService);
        order.verify(authenticationService).authenticate("carl.coach", "pass");
        order.verify(trainerService).setActive("carl.coach", true);
    }

    @Test
    void deleteTraineeProfileShouldAuthenticateBeforeDeleting() {
        gymFacade.deleteTraineeProfile("john.doe", "pass");

        InOrder order = inOrder(authenticationService, traineeService);
        order.verify(authenticationService).authenticate("john.doe", "pass");
        order.verify(traineeService).deleteByUsername("john.doe");
    }

    @Test
    void getTraineeTrainingsShouldAuthenticateBeforeFetching() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 2, 1);
        List<Training> trainings = List.of(new Training());
        when(trainingService.getTraineeTrainings("john.doe", from, to, "trainerName", "CARDIO"))
                .thenReturn(trainings);

        List<Training> result = gymFacade.getTraineeTrainings(
                "john.doe", "pass", from, to, "trainerName", "CARDIO");

        assertEquals(trainings, result);
        InOrder order = inOrder(authenticationService, trainingService);
        order.verify(authenticationService).authenticate("john.doe", "pass");
        order.verify(trainingService).getTraineeTrainings("john.doe", from, to, "trainerName", "CARDIO");
    }

    @Test
    void getTrainerTrainingsShouldAuthenticateBeforeFetching() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 2, 1);
        List<Training> trainings = List.of(new Training());
        when(trainingService.getTrainerTrainings("carl.coach", from, to, "traineeName"))
                .thenReturn(trainings);

        List<Training> result = gymFacade.getTrainerTrainings("carl.coach", "pass", from, to, "traineeName");

        assertEquals(trainings, result);
        InOrder order = inOrder(authenticationService, trainingService);
        order.verify(authenticationService).authenticate("carl.coach", "pass");
        order.verify(trainingService).getTrainerTrainings("carl.coach", from, to, "traineeName");
    }

    @Test
    void addTrainingShouldAuthenticateTraineeBeforeAdding() {
        Training training = new Training();
        when(trainingService.addTraining(training)).thenReturn(training);

        Training result = gymFacade.addTraining("john.doe", "pass", training);

        assertEquals(training, result);
        InOrder order = inOrder(authenticationService, trainingService);
        order.verify(authenticationService).authenticate("john.doe", "pass");
        order.verify(trainingService).addTraining(training);
    }

    @Test
    void getTrainersNotAssignedToTraineeShouldAuthenticateBeforeFetching() {
        List<Trainer> trainers = List.of(new Trainer());
        when(trainerService.getTrainersNotAssignedToTrainee("john.doe")).thenReturn(trainers);

        List<Trainer> result = gymFacade.getTrainersNotAssignedToTrainee("john.doe", "pass");

        assertEquals(trainers, result);
        InOrder order = inOrder(authenticationService, trainerService);
        order.verify(authenticationService).authenticate("john.doe", "pass");
        order.verify(trainerService).getTrainersNotAssignedToTrainee("john.doe");
    }

    @Test
    void updateTraineeTrainersListShouldAuthenticateBeforeUpdating() {
        List<String> usernames = List.of("t1", "t2");
        List<Trainer> updated = List.of(new Trainer());
        when(traineeService.updateTrainersList("john.doe", usernames)).thenReturn(updated);

        List<Trainer> result = gymFacade.updateTraineeTrainersList("john.doe", "pass", usernames);

        assertEquals(updated, result);
        InOrder order = inOrder(authenticationService, traineeService);
        order.verify(authenticationService).authenticate("john.doe", "pass");
        order.verify(traineeService).updateTrainersList("john.doe", usernames);
    }
}
