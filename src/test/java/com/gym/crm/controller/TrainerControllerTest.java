package com.gym.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.controller.support.MockMvcTestSupport;
import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.TrainingType;
import com.gym.crm.domain.User;
import com.gym.crm.dto.common.ActiveStatusRequest;
import com.gym.crm.dto.trainer.TrainerRegistrationRequest;
import com.gym.crm.dto.trainer.UpdateTrainerRequest;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.ValidationException;
import com.gym.crm.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {

    @Mock
    private TrainerService trainerService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = MockMvcTestSupport.objectMapper();
        TrainerController controller = MockMvcTestSupport.withMethodValidation(
                new TrainerController(trainerService), TrainerController.class);
        mockMvc = MockMvcTestSupport.mockMvc(controller);
    }

    private TrainingType cardio() {
        TrainingType type = new TrainingType();
        type.setId(1L);
        type.setTrainingTypeName("CARDIO");
        return type;
    }

    private Trainer trainerWithUsername(String username) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName("Carl");
        user.setLastName("Coach");
        user.setActive(true);
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(cardio());
        return trainer;
    }

    @Test
    void registerShouldReturnCreatedWithGeneratedCredentials() throws Exception {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest();
        request.setFirstName("Carl");
        request.setLastName("Coach");
        request.setSpecializationId(1L);

        when(trainerService.getSpecializationById(1L)).thenReturn(cardio());
        Trainer saved = trainerWithUsername("carl.coach");
        saved.getUser().setPassword("generatedPass");
        when(trainerService.createTrainerProfile(any(Trainer.class))).thenReturn(saved);

        mockMvc.perform(post("/api/trainers")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("carl.coach"))
                .andExpect(jsonPath("$.password").value("generatedPass"));
    }

    @Test
    void registerShouldReturnBadRequestWhenSpecializationIdIsMissing() throws Exception {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest();
        request.setFirstName("Carl");
        request.setLastName("Coach");

        mockMvc.perform(post("/api/trainers")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(trainerService, never()).createTrainerProfile(any());
    }

    @Test
    void registerShouldReturnBadRequestWhenSpecializationIdIsInvalid() throws Exception {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest();
        request.setFirstName("Carl");
        request.setLastName("Coach");
        request.setSpecializationId(999L);

        when(trainerService.getSpecializationById(999L))
                .thenThrow(new ValidationException("Invalid specialization id: 999"));

        mockMvc.perform(post("/api/trainers")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(trainerService, never()).createTrainerProfile(any());
    }

    @Test
    void getProfileShouldReturnTrainerProfileWhenFound() throws Exception {
        when(trainerService.getByUsername("carl.coach")).thenReturn(trainerWithUsername("carl.coach"));

        mockMvc.perform(get("/api/trainers/carl.coach"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Carl"))
                .andExpect(jsonPath("$.specialization.trainingTypeName").value("CARDIO"))
                .andExpect(jsonPath("$.username").doesNotExist());
    }

    @Test
    void getProfileShouldReturnNotFoundWhenTrainerMissing() throws Exception {
        when(trainerService.getByUsername("nobody")).thenThrow(new EntityNotFoundException("Trainer not found: nobody"));

        mockMvc.perform(get("/api/trainers/nobody"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProfileShouldReturnUpdatedProfileWithCarriedOverSpecialization() throws Exception {
        UpdateTrainerRequest request = new UpdateTrainerRequest();
        request.setFirstName("Carlos");
        request.setLastName("Coach");
        request.setActive(true);

        Trainer current = trainerWithUsername("carl.coach");
        when(trainerService.getByUsername("carl.coach")).thenReturn(current);

        Trainer updated = trainerWithUsername("carl.coach");
        updated.getUser().setFirstName("Carlos");
        when(trainerService.updateTrainerProfile(eq("carl.coach"), any(Trainer.class))).thenReturn(updated);

        mockMvc.perform(put("/api/trainers/carl.coach")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("carl.coach"))
                .andExpect(jsonPath("$.firstName").value("Carlos"));
    }

    @Test
    void updateProfileShouldReturnBadRequestWhenLastNameIsMissing() throws Exception {
        UpdateTrainerRequest request = new UpdateTrainerRequest();
        request.setFirstName("Carlos");
        request.setActive(true);

        mockMvc.perform(put("/api/trainers/carl.coach")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(trainerService, never()).updateTrainerProfile(any(), any());
    }

    @Test
    void setActiveShouldReturnOkAndDelegateToService() throws Exception {
        ActiveStatusRequest request = new ActiveStatusRequest(false);

        mockMvc.perform(patch("/api/trainers/carl.coach/status")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(trainerService).setActive("carl.coach", false);
    }

    @Test
    void setActiveShouldReturnBadRequestWhenActiveIsMissing() throws Exception {
        ActiveStatusRequest request = new ActiveStatusRequest(null);

        mockMvc.perform(patch("/api/trainers/carl.coach/status")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(trainerService, never()).setActive(any(), org.mockito.ArgumentMatchers.anyBoolean());
    }
}