package com.gym.crm.dto;

import com.gym.crm.dto.auth.ChangeLoginRequest;
import com.gym.crm.dto.auth.LoginRequest;
import com.gym.crm.dto.common.ActiveStatusRequest;
import com.gym.crm.dto.trainee.TraineeRegistrationRequest;
import com.gym.crm.dto.trainee.UpdateTraineeRequest;
import com.gym.crm.dto.trainee.UpdateTraineeTrainersRequest;
import com.gym.crm.dto.trainer.TrainerRegistrationRequest;
import com.gym.crm.dto.trainer.UpdateTrainerRequest;
import com.gym.crm.dto.training.AddTrainingRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    private TraineeRegistrationRequest validTraineeRegistration() {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        return request;
    }

    @Test
    void traineeRegistrationShouldBeValidWithOnlyRequiredFields() {
        Set<ConstraintViolation<TraineeRegistrationRequest>> violations =
                validator.validate(validTraineeRegistration());

        assertTrue(violations.isEmpty());
    }

    @Test
    void traineeRegistrationShouldBeValidWithOptionalFieldsPopulated() {
        TraineeRegistrationRequest request = validTraineeRegistration();
        request.setDateOfBirth(LocalDate.of(1995, 5, 20));
        request.setAddress("123 Main St");

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void traineeRegistrationShouldRejectBlankFirstName() {
        TraineeRegistrationRequest request = validTraineeRegistration();
        request.setFirstName("  ");

        assertEquals(1, validator.validate(request).size());
    }

    @Test
    void traineeRegistrationShouldRejectMissingLastName() {
        TraineeRegistrationRequest request = validTraineeRegistration();
        request.setLastName(null);

        assertEquals(1, validator.validate(request).size());
    }

    private UpdateTraineeRequest validUpdateTraineeRequest() {
        UpdateTraineeRequest request = new UpdateTraineeRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setActive(true);
        return request;
    }

    @Test
    void updateTraineeRequestShouldBeValidWithAllRequiredFields() {
        assertTrue(validator.validate(validUpdateTraineeRequest()).isEmpty());
    }

    @Test
    void updateTraineeRequestShouldRejectMissingActive() {
        UpdateTraineeRequest request = validUpdateTraineeRequest();
        request.setActive(null);

        assertEquals(1, validator.validate(request).size());
    }

    @Test
    void updateTraineeTrainersRequestShouldRejectEmptyTrainersList() {
        UpdateTraineeTrainersRequest request = new UpdateTraineeTrainersRequest();
        request.setTrainers(List.of());

        assertEquals(1, validator.validate(request).size());
    }

    @Test
    void updateTraineeTrainersRequestShouldRejectBlankTrainerUsername() {
        UpdateTraineeTrainersRequest.TrainerUsername trainerUsername =
                new UpdateTraineeTrainersRequest.TrainerUsername();
        trainerUsername.setTrainerUsername(" ");
        UpdateTraineeTrainersRequest request = new UpdateTraineeTrainersRequest();
        request.setTrainers(List.of(trainerUsername));

        assertEquals(1, validator.validate(request).size());
    }

    @Test
    void updateTraineeTrainersRequestShouldBeValidWithAtLeastOneTrainer() {
        UpdateTraineeTrainersRequest.TrainerUsername trainerUsername =
                new UpdateTraineeTrainersRequest.TrainerUsername();
        trainerUsername.setTrainerUsername("carl.coach");
        UpdateTraineeTrainersRequest request = new UpdateTraineeTrainersRequest();
        request.setTrainers(List.of(trainerUsername));

        assertTrue(validator.validate(request).isEmpty());
    }

    private TrainerRegistrationRequest validTrainerRegistration() {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest();
        request.setFirstName("Carl");
        request.setLastName("Coach");
        request.setSpecializationId(1L);
        return request;
    }

    @Test
    void trainerRegistrationShouldBeValidWithAllRequiredFields() {
        assertTrue(validator.validate(validTrainerRegistration()).isEmpty());
    }

    @Test
    void trainerRegistrationShouldRejectMissingSpecializationId() {
        TrainerRegistrationRequest request = validTrainerRegistration();
        request.setSpecializationId(null);

        assertEquals(1, validator.validate(request).size());
    }

    private UpdateTrainerRequest validUpdateTrainerRequest() {
        UpdateTrainerRequest request = new UpdateTrainerRequest();
        request.setFirstName("Carl");
        request.setLastName("Coach");
        request.setActive(true);
        return request;
    }

    @Test
    void updateTrainerRequestShouldBeValidWithAllRequiredFields() {
        assertTrue(validator.validate(validUpdateTrainerRequest()).isEmpty());
    }

    @Test
    void updateTrainerRequestShouldRejectMissingActive() {
        UpdateTrainerRequest request = validUpdateTrainerRequest();
        request.setActive(null);

        assertEquals(1, validator.validate(request).size());
    }

    private AddTrainingRequest validAddTrainingRequest() {
        AddTrainingRequest request = new AddTrainingRequest();
        request.setTraineeUsername("john.doe");
        request.setTrainerUsername("carl.coach");
        request.setTrainingName("Morning Cardio");
        request.setTrainingDate(LocalDate.of(2026, 8, 1));
        request.setTrainingDuration(60);
        return request;
    }

    @Test
    void addTrainingRequestShouldBeValidWithAllRequiredFields() {
        assertTrue(validator.validate(validAddTrainingRequest()).isEmpty());
    }

    @Test
    void addTrainingRequestShouldRejectZeroDuration() {
        AddTrainingRequest request = validAddTrainingRequest();
        request.setTrainingDuration(0);

        assertEquals(1, validator.validate(request).size());
    }

    @Test
    void addTrainingRequestShouldRejectNegativeDuration() {
        AddTrainingRequest request = validAddTrainingRequest();
        request.setTrainingDuration(-5);

        assertEquals(1, validator.validate(request).size());
    }

    @Test
    void addTrainingRequestShouldRejectMissingTrainingDate() {
        AddTrainingRequest request = validAddTrainingRequest();
        request.setTrainingDate(null);

        assertEquals(1, validator.validate(request).size());
    }

    @Test
    void changeLoginRequestShouldBeValidWithAllRequiredFields() {
        ChangeLoginRequest request = new ChangeLoginRequest("john.doe", "oldPass", "newPass");

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void changeLoginRequestShouldRejectBlankNewPassword() {
        ChangeLoginRequest request = new ChangeLoginRequest("john.doe", "oldPass", "");

        assertEquals(1, validator.validate(request).size());
    }

    @Test
    void loginRequestShouldBeValidWithUsernameAndPassword() {
        LoginRequest request = new LoginRequest("john.doe", "secret");

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void loginRequestShouldRejectBlankUsername() {
        LoginRequest request = new LoginRequest("", "secret");

        assertEquals(1, validator.validate(request).size());
    }

    @Test
    void loginRequestShouldRejectBlankPassword() {
        LoginRequest request = new LoginRequest("john.doe", "");

        assertEquals(1, validator.validate(request).size());
    }

    @Test
    void activeStatusRequestShouldBeValidWhenActiveIsSet() {
        ActiveStatusRequest request = new ActiveStatusRequest(true);

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void activeStatusRequestShouldRejectMissingActive() {
        ActiveStatusRequest request = new ActiveStatusRequest(null);

        assertEquals(1, validator.validate(request).size());
    }
}