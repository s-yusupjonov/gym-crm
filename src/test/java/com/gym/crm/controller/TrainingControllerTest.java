package com.gym.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.controller.support.MockMvcTestSupport;
import com.gym.crm.domain.Trainee;
import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.Training;
import com.gym.crm.domain.TrainingType;
import com.gym.crm.domain.User;
import com.gym.crm.dto.training.AddTrainingRequest;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {

    @Mock
    private TrainingService trainingService;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = MockMvcTestSupport.objectMapper();
        TrainingController controller = MockMvcTestSupport.withMethodValidation(
                new TrainingController(trainingService, traineeService, trainerService), TrainingController.class);
        mockMvc = MockMvcTestSupport.mockMvc(controller);
    }

    private TrainingType cardio() {
        TrainingType type = new TrainingType();
        type.setId(1L);
        type.setTrainingTypeName("CARDIO");
        return type;
    }

    private Trainee trainee(String username) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName("John");
        user.setLastName("Doe");
        Trainee trainee = new Trainee();
        trainee.setUser(user);
        return trainee;
    }

    private Trainer trainer(String username) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName("Carl");
        user.setLastName("Coach");
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(cardio());
        return trainer;
    }

    private Training training(Trainee trainee, Trainer trainer) {
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName("Morning Cardio");
        training.setTrainingDate(LocalDate.of(2026, 8, 1));
        training.setTrainingDuration(60);
        training.setTrainingType(cardio());
        return training;
    }

    @Test
    void addTrainingShouldReturnOkWhenValid() throws Exception {
        AddTrainingRequest request = new AddTrainingRequest();
        request.setTraineeUsername("john.doe");
        request.setTrainerUsername("carl.coach");
        request.setTrainingName("Morning Cardio");
        request.setTrainingDate(LocalDate.of(2026, 8, 1));
        request.setTrainingDuration(60);

        Trainee trainee = trainee("john.doe");
        Trainer trainer = trainer("carl.coach");
        when(traineeService.getByUsername("john.doe")).thenReturn(trainee);
        when(trainerService.getByUsername("carl.coach")).thenReturn(trainer);

        mockMvc.perform(post("/api/trainings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(trainingService).addTraining(any(Training.class));
    }

    @Test
    void addTrainingShouldReturnBadRequestWhenDurationIsNotPositive() throws Exception {
        AddTrainingRequest request = new AddTrainingRequest();
        request.setTraineeUsername("john.doe");
        request.setTrainerUsername("carl.coach");
        request.setTrainingName("Morning Cardio");
        request.setTrainingDate(LocalDate.of(2026, 8, 1));
        request.setTrainingDuration(0);

        mockMvc.perform(post("/api/trainings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(trainingService, never()).addTraining(any());
    }

    @Test
    void addTrainingShouldReturnNotFoundWhenTraineeMissing() throws Exception {
        AddTrainingRequest request = new AddTrainingRequest();
        request.setTraineeUsername("nobody");
        request.setTrainerUsername("carl.coach");
        request.setTrainingName("Morning Cardio");
        request.setTrainingDate(LocalDate.of(2026, 8, 1));
        request.setTrainingDuration(60);

        when(traineeService.getByUsername("nobody")).thenThrow(new EntityNotFoundException("Trainee not found: nobody"));

        mockMvc.perform(post("/api/trainings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(trainingService, never()).addTraining(any());
    }

    @Test
    void getTraineeTrainingsShouldReturnMappedList() throws Exception {
        Trainee trainee = trainee("john.doe");
        Trainer trainer = trainer("carl.coach");
        when(traineeService.getByUsername("john.doe")).thenReturn(trainee);
        when(trainingService.getTraineeTrainings(eq("john.doe"), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(List.of(training(trainee, trainer)));

        mockMvc.perform(get("/api/trainings/trainee/john.doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].trainingName").value("Morning Cardio"))
                .andExpect(jsonPath("$[0].trainerName").value("Carl Coach"))
                .andExpect(jsonPath("$[0].trainingType").value("CARDIO"));
    }

    @Test
    void getTraineeTrainingsShouldPassFiltersThrough() throws Exception {
        Trainee trainee = trainee("john.doe");
        when(traineeService.getByUsername("john.doe")).thenReturn(trainee);
        when(trainingService.getTraineeTrainings(eq("john.doe"), eq(LocalDate.of(2026, 1, 1)),
                eq(LocalDate.of(2026, 12, 31)), eq("Carl"), eq("CARDIO")))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/trainings/trainee/john.doe")
                        .param("periodFrom", "2026-01-01")
                        .param("periodTo", "2026-12-31")
                        .param("trainerName", "Carl")
                        .param("trainingType", "CARDIO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getTraineeTrainingsShouldReturnNotFoundWhenTraineeMissing() throws Exception {
        when(traineeService.getByUsername("nobody")).thenThrow(new EntityNotFoundException("Trainee not found: nobody"));

        mockMvc.perform(get("/api/trainings/trainee/nobody"))
                .andExpect(status().isNotFound());

        verify(trainingService, never()).getTraineeTrainings(anyString(), any(), any(), any(), any());
    }

    @Test
    void getTrainerTrainingsShouldReturnMappedList() throws Exception {
        Trainee trainee = trainee("john.doe");
        Trainer trainer = trainer("carl.coach");
        when(trainerService.getByUsername("carl.coach")).thenReturn(trainer);
        when(trainingService.getTrainerTrainings(eq("carl.coach"), isNull(), isNull(), isNull()))
                .thenReturn(List.of(training(trainee, trainer)));

        mockMvc.perform(get("/api/trainings/trainer/carl.coach"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].traineeName").value("John Doe"));
    }

    @Test
    void getTrainerTrainingsShouldReturnNotFoundWhenTrainerMissing() throws Exception {
        when(trainerService.getByUsername("nobody")).thenThrow(new EntityNotFoundException("Trainer not found: nobody"));

        mockMvc.perform(get("/api/trainings/trainer/nobody"))
                .andExpect(status().isNotFound());

        verify(trainingService, never()).getTrainerTrainings(anyString(), any(), any(), any());
    }

    @Test
    void getTrainingTypesShouldReturnAllTypes() throws Exception {
        when(trainingService.getTrainingTypes()).thenReturn(List.of(cardio()));

        mockMvc.perform(get("/api/trainings/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].trainingTypeName").value("CARDIO"));
    }
}