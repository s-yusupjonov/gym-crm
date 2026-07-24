package com.gym.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.controller.support.MockMvcTestSupport;
import com.gym.crm.domain.Trainee;
import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.TrainingType;
import com.gym.crm.domain.User;
import com.gym.crm.dto.common.ActiveStatusRequest;
import com.gym.crm.dto.trainee.TraineeRegistrationRequest;
import com.gym.crm.dto.trainee.UpdateTraineeRequest;
import com.gym.crm.dto.trainee.UpdateTraineeTrainersRequest;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = MockMvcTestSupport.objectMapper();
        TraineeController controller = MockMvcTestSupport.withMethodValidation(
                new TraineeController(traineeService, trainerService), TraineeController.class);
        mockMvc = MockMvcTestSupport.mockMvc(controller);
    }

    private Trainee traineeWithUsername(String username) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setActive(true);
        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setAddress("Main St");
        trainee.setDateOfBirth(LocalDate.of(1995, 5, 20));
        return trainee;
    }

    private Trainer trainerWithUsername(String username) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName("Carl");
        user.setLastName("Coach");
        user.setActive(true);
        TrainingType type = new TrainingType();
        type.setId(1L);
        type.setTrainingTypeName("CARDIO");
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(type);
        return trainer;
    }

    @Test
    void registerShouldReturnCreatedWithGeneratedCredentials() throws Exception {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");

        Trainee saved = traineeWithUsername("john.doe");
        saved.getUser().setPassword("generatedPass");
        when(traineeService.createTraineeProfile(any(Trainee.class))).thenReturn(saved);

        mockMvc.perform(post("/api/trainees")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.password").value("generatedPass"));
    }

    @Test
    void registerShouldReturnBadRequestWhenFirstNameIsMissing() throws Exception {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest();
        request.setLastName("Doe");

        mockMvc.perform(post("/api/trainees")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(traineeService, never()).createTraineeProfile(any());
    }

    @Test
    void getProfileShouldReturnTraineeProfileWhenFound() throws Exception {
        when(traineeService.getByUsername("john.doe")).thenReturn(traineeWithUsername("john.doe"));

        mockMvc.perform(get("/api/trainees/john.doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.username").doesNotExist());
    }

    @Test
    void getProfileShouldReturnNotFoundWhenTraineeMissing() throws Exception {
        when(traineeService.getByUsername("nobody")).thenThrow(new EntityNotFoundException("Trainee not found: nobody"));

        mockMvc.perform(get("/api/trainees/nobody"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trainee not found: nobody"));
    }

    @Test
    void updateProfileShouldReturnUpdatedProfile() throws Exception {
        UpdateTraineeRequest request = new UpdateTraineeRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setActive(true);

        Trainee updated = traineeWithUsername("john.doe");
        updated.getUser().setFirstName("Jane");
        when(traineeService.updateTraineeProfile(eq("john.doe"), any(Trainee.class))).thenReturn(updated);

        mockMvc.perform(put("/api/trainees/john.doe")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    void updateProfileShouldReturnBadRequestWhenActiveIsMissing() throws Exception {
        UpdateTraineeRequest request = new UpdateTraineeRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");

        mockMvc.perform(put("/api/trainees/john.doe")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(traineeService, never()).updateTraineeProfile(any(), any());
    }

    @Test
    void deleteShouldReturnOkWhenTraineeDeleted() throws Exception {
        mockMvc.perform(delete("/api/trainees/john.doe"))
                .andExpect(status().isOk());

        verify(traineeService).deleteByUsername("john.doe");
    }

    @Test
    void deleteShouldReturnNotFoundWhenTraineeMissing() throws Exception {
        org.mockito.Mockito.doThrow(new EntityNotFoundException("Trainee not found: nobody"))
                .when(traineeService).deleteByUsername("nobody");

        mockMvc.perform(delete("/api/trainees/nobody"))
                .andExpect(status().isNotFound());
    }

    @Test
    void setActiveShouldReturnOkAndDelegateToService() throws Exception {
        ActiveStatusRequest request = new ActiveStatusRequest(false);

        mockMvc.perform(patch("/api/trainees/john.doe/status")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(traineeService).setActive("john.doe", false);
    }

    @Test
    void setActiveShouldReturnBadRequestWhenActiveIsMissing() throws Exception {
        ActiveStatusRequest request = new ActiveStatusRequest(null);

        mockMvc.perform(patch("/api/trainees/john.doe/status")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(traineeService, never()).setActive(any(), org.mockito.ArgumentMatchers.anyBoolean());
    }

    @Test
    void getUnassignedActiveTrainersShouldReturnOnlyActiveTrainers() throws Exception {
        when(traineeService.getByUsername("john.doe")).thenReturn(traineeWithUsername("john.doe"));

        Trainer active = trainerWithUsername("active.trainer");
        Trainer inactive = trainerWithUsername("inactive.trainer");
        inactive.getUser().setActive(false);
        when(trainerService.getTrainersNotAssignedToTrainee("john.doe")).thenReturn(List.of(active, inactive));

        mockMvc.perform(get("/api/trainees/john.doe/unassigned-trainers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username").value("active.trainer"));
    }

    @Test
    void getUnassignedActiveTrainersShouldReturnNotFoundWhenTraineeMissing() throws Exception {
        when(traineeService.getByUsername("nobody")).thenThrow(new EntityNotFoundException("Trainee not found: nobody"));

        mockMvc.perform(get("/api/trainees/nobody/unassigned-trainers"))
                .andExpect(status().isNotFound());

        verify(trainerService, never()).getTrainersNotAssignedToTrainee(any());
    }

    @Test
    void updateTrainersListShouldReturnUpdatedTrainerList() throws Exception {
        UpdateTraineeTrainersRequest.TrainerUsername trainerUsername =
                new UpdateTraineeTrainersRequest.TrainerUsername();
        trainerUsername.setTrainerUsername("carl.coach");
        UpdateTraineeTrainersRequest request = new UpdateTraineeTrainersRequest();
        request.setTrainers(List.of(trainerUsername));

        when(traineeService.updateTrainersList(eq("john.doe"), anyList()))
                .thenReturn(List.of(trainerWithUsername("carl.coach")));

        mockMvc.perform(put("/api/trainees/john.doe/trainers")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username").value("carl.coach"));
    }

    @Test
    void updateTrainersListShouldReturnBadRequestWhenTrainersListIsEmpty() throws Exception {
        UpdateTraineeTrainersRequest request = new UpdateTraineeTrainersRequest();
        request.setTrainers(List.of());

        mockMvc.perform(put("/api/trainees/john.doe/trainers")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(traineeService, never()).updateTrainersList(any(), anyList());
    }
}