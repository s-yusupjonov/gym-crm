package com.gym.crm.mapper;

import com.gym.crm.domain.Trainee;
import com.gym.crm.domain.Trainer;
import com.gym.crm.domain.TrainingType;
import com.gym.crm.domain.User;
import com.gym.crm.dto.trainee.TraineeProfileResponse;
import com.gym.crm.dto.trainee.TraineeRegistrationRequest;
import com.gym.crm.dto.trainee.UpdateTraineeRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TraineeMapperTest {

    @Test
    void toEntityFromRegistrationRequestShouldCopyAllFields() {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDateOfBirth(LocalDate.of(1995, 5, 20));
        request.setAddress("123 Main St");

        Trainee trainee = TraineeMapper.toEntity(request);

        assertEquals("John", trainee.getUser().getFirstName());
        assertEquals("Doe", trainee.getUser().getLastName());
        assertEquals(LocalDate.of(1995, 5, 20), trainee.getDateOfBirth());
        assertEquals("123 Main St", trainee.getAddress());
    }

    @Test
    void toEntityFromUpdateRequestShouldCopyActiveFlag() {
        UpdateTraineeRequest request = new UpdateTraineeRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setActive(true);

        Trainee trainee = TraineeMapper.toEntity(request);

        assertTrue(trainee.getUser().isActive());
    }

    @Test
    void toEntityFromUpdateRequestShouldTreatNullActiveAsFalse() {
        UpdateTraineeRequest request = new UpdateTraineeRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");

        Trainee trainee = TraineeMapper.toEntity(request);

        assertFalse(trainee.getUser().isActive());
    }

    @Test
    void toProfileResponseShouldOmitUsernameAndIncludeTrainers() {
        Trainee trainee = new Trainee();
        User user = new User();
        user.setUsername("john.doe");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setActive(true);
        trainee.setUser(user);
        trainee.setDateOfBirth(LocalDate.of(1995, 5, 20));
        trainee.setAddress("123 Main St");

        Trainer trainer = new Trainer();
        User trainerUser = new User();
        trainerUser.setUsername("carl.coach");
        trainerUser.setFirstName("Carl");
        trainerUser.setLastName("Coach");
        trainer.setUser(trainerUser);
        TrainingType type = new TrainingType();
        type.setId(1L);
        type.setTrainingTypeName("CARDIO");
        trainer.setSpecialization(type);
        trainee.setTrainers(Set.of(trainer));

        TraineeProfileResponse response = TraineeMapper.toProfileResponse(trainee);

        assertNull(response.getUsername());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertTrue(response.isActive());
        assertEquals(1, response.getTrainers().size());
        assertEquals("carl.coach", response.getTrainers().get(0).getUsername());
    }

    @Test
    void toUpdateResponseShouldIncludeUsername() {
        Trainee trainee = new Trainee();
        User user = new User();
        user.setUsername("john.doe");
        user.setFirstName("John");
        user.setLastName("Doe");
        trainee.setUser(user);
        trainee.setTrainers(Set.of());

        TraineeProfileResponse response = TraineeMapper.toUpdateResponse(trainee);

        assertEquals("john.doe", response.getUsername());
    }
}